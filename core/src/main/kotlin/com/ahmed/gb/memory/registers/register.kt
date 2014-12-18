package com.ahmed.gb.memory.registers

import com.ahmed.gb.memory.memory

trait register : memory
{
    fun inc() : register
    fun dec() : register
    fun plusAssign(other : register)
    fun plusAssign(other : Int)
    fun minusAssign(other : register)
    fun minusAssign(other : Int)
}