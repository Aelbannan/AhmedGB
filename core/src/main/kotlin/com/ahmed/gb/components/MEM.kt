package com.ahmed.gb.components

import com.ahmed.gb.GameBoy
import com.ahmed.gb.components.PPU
import com.ahmed.gb.PRGM
import java.io.File
import java.io.FileInputStream

class MEM (val device : GameBoy)
{
    val cpu : DMG
        get() = device.cpu
    val gpu : PPU
        get() = device.gpu

    // RAM r/w interfaces
    val rom = ROM()
    val ram = RAM()

    inner class RAM
    {
        private val eram =  ByteArray(0x2000)    // external RAM in cart, 8k
        private val wram =  ByteArray(0x2000)    // working RAM, 8k
        private val zram =  ByteArray(0x0080)    // super fast page, 128b

        public fun get(addr : Int) : Byte
        {
            cpu.clk.m += 1
            cpu.clk.t += 4

            return when(addr and 0xF000)
            {
                0x8000,0x9000               -> gpu.vram[addr and 0x1FFF]// VRAM
                0xA000,0xB000               -> $eram[addr and 0x1FFF]   // ERAM
                0xC000,0xD000,0xE000        -> $wram[addr and 0x1FFF]   // WRAM + 1/2 of WRAM Shadow
                0xF000                      -> ziowAccess(addr)         // WRAM shadow, I/O, Zero page
                else                        -> 0
            }
        }

        // used to check high part of WRAM shadow, IO memory, & Zero page
        private inline fun ziowAccess(addr : Int) : Byte
        {
            return when (addr and 0x0F00)
            {
            // GPU oam: only first 160 bytes are legit, others are 0
                0x0E00  -> (if (addr < 0xFEA0) gpu.oam[addr and 0xFF] else 0)
            // Zero page : 128 bytes, no i/o control currently
                0x0F00  -> (if (addr >= 0xFF80) $zram[addr and 0x07F] else 0)
            // WRAM shadow
                else    -> $wram[addr and 0x1FFF]
            }
        }

        public fun set(addr : Int, data : Byte)
        {
            when(addr and 0xF000)
            {
                0x8000,0x9000           -> gpu.vram[addr and 0x1FFF] = data // VRAM
                0xA000,0xB000           -> $eram[addr and 0x1FFF] = data    // ERAM
                0xC000,0xD000,0xE000    -> $wram[addr and 0x1FFF] = data    // WRAM + 1/2 of WRAM Shadow
                0xF000                  -> ziowWrite(addr,data)             // WRAM shadow, I/O, Zero page
            }
        }

        // used to check high part of WRAM shadow, IO memory, & Zero page
        private inline fun ziowWrite(addr : Int, data : Byte)
        {
            when (addr and 0x0F00)
            {
            // GPU oam: only first 160 bytes are legit, others are 0
                0xE00   -> if (addr < 0xFEA0) gpu.oam[addr and 0xFF] = data
            // Zero page : 128 bytes, no i/o control currently
                0xF00   -> if (addr >= 0xFF80) $zram[addr and 0x07F] = data
            // WRAM shadow
                0x000   -> $wram[addr and 0x1FFF] = data
                else    -> $wram[addr and 0x1FFF] = data
            }
        }
    }

    // Read only Memory
    inner class ROM
    {
        // if we are still reading the bios
        private var inBios = true;

        // rom pages/sections
        private val bios =  ByteArray(0x0100)    // bios, 256b
        private val rom =   ByteArray(0x8000)    // rom bank, 32k w 16k switch

        // check which section to get
        public fun get(addr : Int) : Byte
        {
            return when(addr and 0xF000)
            {
                0x0000                      -> biosAccess(addr)  // bios & ROM0
                0x1000,0x2000,0x3000        -> $rom[addr]        // ROM0
                0x4000,0x5000,0x6000,0x7000 -> $rom[addr]        // ROM1 (unbanked)
                else                        -> 0
            }
        }

        // used to check the low 8k of memory
        private inline fun biosAccess(addr : Int) : Byte
        {
            if ($inBios)
            {
                if (addr < 0x0100)
                    return $bios[addr]
                else if (cpu.reg.pc.get() == 0x100)
                    $inBios = false
            }

            return $rom[addr]
        }

        public fun loadBIOS()
        {
            for (x in 0..PRGM.bios.size-1)
                $bios[x] = PRGM.bios[x].toByte()
        }

        public fun loadROM()
        {
            val file = File("Dr. Mario.gb")
            val fin = FileInputStream(file)

            fin.read(rom)

            println(file.length())
        }
    }

    public fun get (addr : Int) : Byte
    {
        return if (addr < 0x8000) rom[addr] else ram[addr]
    }

    public fun set (addr : Int, data : Byte)
    {
        if (addr >= 0x8000) ram[addr] = data
    }
}