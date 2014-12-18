package com.ahmed.gb.memory.registers

import com.ahmed.gb.memory.memory
import com.ahmed.gb.memory.wordSized

/**
 * Created by Ahmed on 12/16/2014.
 * Register pair (2 8bit registers)
 */
class preg(val name : String, val a : sreg, val b : sreg) : register, wordSized
{
    override fun get() = (a.get() shl 8) + b.get()
    override fun set(value: Int)
    {
        a.set(value shr 8)
        b.set(value)
    }
    override fun inc() : preg { set(get()+1); return this }
    override fun dec() : preg { set(get()-1); return this }
    override fun plusAssign(other : register) { set(get() + other.get()) }
    override fun minusAssign(other : register) { set(get() - other.get()) }
    override fun plusAssign(other : Int) { set(get() + other) }
    override fun minusAssign(other : Int) { set(get() - other) }
    override fun toString() = "{PAIR $name = ${get()}}"
}