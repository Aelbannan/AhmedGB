package com.ahmed.gb.memory.registers

import com.ahmed.gb.memory.memory

/**
 * Created by Ahmed on 12/16/2014.
 * 8-bit register
 */
open class sreg(val name : String) : register
{
    protected var data : Byte = 0

    override fun get() = ($data.toInt()) and 0xff
    override fun set(value: Int) { $data = value.toByte() }
    override fun inc() : sreg { set($data+1); return this }
    override fun dec() : sreg { set($data-1); return this }
    override fun plusAssign(other : register) { set($data + other.get()) }
    override fun minusAssign(other : register) { set($data - other.get()) }
    override fun plusAssign(other : Int) { set($data + other) }
    override fun minusAssign(other : Int) { set($data - other) }
    override fun toString() = "{SREG %$name = ${data.toInt() and 0xff}}"
}