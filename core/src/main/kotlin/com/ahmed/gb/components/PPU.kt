package com.ahmed.gb.components

import com.ahmed.gb.GameBoy

/**
 * Created by Ahmed on 12/16/2014.
 * GPU..
 */
class PPU(val device : GameBoy)
{
    public var clock : Int = 0

    val vblank = 0x40


    // object attribute memory. (used by CPU to interface to GPU)
    val oam = ByteArray(0x00A0)
    val vram =  ByteArray(0x2000)    // GPU VRAM, 8k

    //val screen : Pixmap = PRGM.getScreenTex()

    public fun update()
    {

    }
}