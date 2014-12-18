package com.ahmed.gb.memory.ram

import com.ahmed.gb.memory.memory
import com.ahmed.gb.memory.registers.register

trait ram : memory
{
    val addr : Int

    fun write(reg : register)
    fun read() : Int
}