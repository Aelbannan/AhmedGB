package com.ahmed.gb

import com.ahmed.gb.components.DMG
import com.ahmed.gb.components.PPU
import com.ahmed.gb.components.MEM
import com.ahmed.gb.components.MMU

/**
 * Created by Ahmed on 12/16/2014.
 */

class GameBoy
{
    val cpu = DMG(this)
    val gpu = PPU(this)
    val mem = MEM(this)
    val mmu = MMU(this)

    public fun begin()
    {
        mem.rom.loadBIOS()
        mem.rom.loadROM()
    }

    public fun update()
    {
        cpu.run()
    }
}