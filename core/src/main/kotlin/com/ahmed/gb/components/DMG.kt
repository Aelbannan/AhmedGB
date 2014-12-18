package com.ahmed.gb.components

import com.ahmed.gb.GameBoy
import com.ahmed.gb.memory.registers.sreg
import com.ahmed.gb.memory.registers.freg
import com.ahmed.gb.memory.registers.dreg
import com.ahmed.gb.memory.registers.preg
import com.ahmed.gb.Decoder

/**
 * Created by Ahmed on 12/16/2014.
 * GameBoy Processor, modified version of Z90 processor
 */
class DMG (val device : GameBoy)
{
    inner class clock
    {
        public var m : Int = 0
        public var t : Int = 0

        public fun invoke(mm : Int, tt : Int)
        {
            m += mm
            t += tt
        }
    }

    inner class registers
    {
        // General Registers (8 bit)
        public var a : sreg = sreg("a")    // accumulator
        public var b : sreg = sreg("b")
        public var c : sreg = sreg("c")
        public var d : sreg = sreg("d")
        public var e : sreg = sreg("e")
        public var h : sreg = sreg("h")
        public var l : sreg = sreg("l")

        // Flags - Zero, Op (is a subtract?), Half-Carry (4 bit), Carry (8-bit)
        public var f : freg = freg("f");

        // 16-Bit Registers
        public var pc : dreg = dreg("pc", 0x0000)
        public var sp : dreg = dreg("sp", 0xFFFE)

        // Register pairs (used for words, etc)
        public var af: preg = preg("af", a, f)
        public var bc: preg = preg("bc", b, c)
        public var de: preg = preg("de", d, e)
        public var hl: preg = preg("hl", h, l)

    }

    var reg = registers()
    var clk = clock()
    var d = Decoder(this)

    var IME = false
    var STOP = false

    fun reset()
    {
        $reg = registers()
        $clk = clock()
    }


    fun run()
    {
        d.decode()
    }
}