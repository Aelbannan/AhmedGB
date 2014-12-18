package com.ahmed.gb.components

import com.ahmed.gb.GameBoy
import com.ahmed.gb.memory.memory
import com.ahmed.gb.memory.ram.ram
import com.ahmed.gb.memory.registers.register
import com.ahmed.gb.memory.wordSized

/**
 * Created by Ahmed on 12/16/2014.
 * Memory Management Unit
 * TODO if needed remove ands here
 */
class MMU(val device : GameBoy)
{
    // interrupts enabled
    val IE = mbyte(0xFFFF)
    // interrupt flags
    val IF = mbyte(0xFF0F)
    // timer register
    // The time counter is kept at 0xFF05.
    // It goes from 0-255. When it exceeds 255, it is loaded with the value at 0xFF06.
    // The timer on/off switch and the frequency is kept at 0xFF07.
    val TIMA = mbyte(0xFF05)
    // you should create a variable to hold the number of cycles
    // until the timer is updated(which is machine speed / timer frequency.
    // example: 1048576 / 4096 = 256) and subtract the opcode's cycles from that.
    // When it is lower than or equal to 0, increment 0xFF05.
    // When 0xFF05 exceeds 255, load it with the contents of 0xFF06.
    // Hope that is what you're talking about.

    val cpu : DMG
        get() = device.cpu
    val mem: MEM
        get() = device.mem

    // read 8 bit byte
    fun rb(addr : Int) = mem[addr].toInt() and 0xff
    // read 16 bit word
    fun rw(addr : Int) : Int
    {
        val hi = (mem[addr+1].toInt() shl 8) and 0xffff
        val lo = mem[addr].toInt() and 0xff

        return (hi+lo)
    }

    // write 8 bit byte
    fun wb(addr : Int, value : Int) { mem[addr] = value.toByte() }
    // write 16 byte word
    fun ww(addr : Int, value : Int)
    {
        mem[addr+1] = (value and 0xffff shr 8).toByte()
        mem[addr] = (value and 0xff).toByte()
    }

    fun inc(addr : memory)
    {
        var i = rb(addr.get())
        i += 1
        wb(addr.get(),i)
    }

    fun dec(addr : memory)
    {
        var i = rb(addr.get())
        i -= 1
        wb(addr.get(),i)
    }

    // 8 bit RAM pointer
    inner class mbyte(override val addr : Int) : ram
    {
        final override fun read() = rb($addr)
        final override fun write(reg : register) = wb($addr, reg.get())
        final override fun get() = rb($addr)
        final override fun set(value : Int)  = wb($addr, value)
        override fun toString() = "[BYTE $addr = ${get()}]"
    }

    // creates mem pointer from register
    fun invoke(mem : memory, size : Int = 1) : ram
    {
        when (mem)
        {
            is wordSized -> return if (size == 2) mword(mem.get()) else mbyte(mem.get())
            is memory    -> return zbyte(mem.get())
            else            -> println("Error. not a register")
        }
        return mbyte(0)
    }

    // 16 bit ram pointer
    inner class mword(override val addr : Int) : ram, wordSized
    {
        final override fun read() = rw($addr)
        final override fun write(reg : register) = ww($addr, reg.get())
        final override fun get() = rw($addr)
        final override fun set(value : Int)  = ww($addr, value)
        override fun toString() = "[WORD $addr = ${get()}]"
        // returns a new address pointer USING the address stored in this memory location
    }

    // 8 bit zero page pointer (addr + 0xFF00)
    inner class zbyte(override val addr : Int) : ram
    {
        final override fun read() = rb(0xFF00 + $addr)
        final override fun write(reg : register) = wb(0xFF00 + $addr, reg.get())
        final override fun get() = rb(0xFF00 + $addr)
        final override fun set(value : Int) = wb(0xFF00 + $addr, value)
        override fun toString() = "[ZP-BYTE $addr = ${get()}]"
    }

    // immediate values
    // for reading next byte/word and changing pc
    val nextb : mbyte
        get()   {
                    val b = mbyte(cpu.reg.pc.get())
                    cpu.reg.pc += 1
                    return b
                }

    val nextw : mword
        get()   {
                    val w = mword(cpu.reg.pc.get())
                    cpu.reg.pc += 2
                    return w
                }

    // address stored in nextw
    val nexta : mword
        get() = mword(nextw.get())
}