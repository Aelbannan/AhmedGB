package com.ahmed.gb.memory.registers

import com.ahmed.gb.memory.memory
import com.ahmed.gb.memory.wordSized

/**
 * Created by Ahmed on 12/16/2014.
 * 16-bit register
 */
class dreg(val name : String, private var data : Int) : register, wordSized
{
    override fun get() = (data.toInt()) and 0xffff
    override fun set(value: Int) { data = value }
    override fun inc() : dreg { set(data+1); return this }
    override fun dec() : dreg { set(data-1); return this }
    override fun plusAssign(other : register) { set(data + other.get()) }
    override fun minusAssign(other : register) { set(data - other.get()) }
    override fun plusAssign(other : Int) { set(data + other) }
    override fun minusAssign(other : Int) { set(data - other) }
    override fun toString() = "{DREG %$name = $data}"
}