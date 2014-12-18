package com.ahmed.gb.memory.registers

/**
 * Created by Ahmed on 12/16/2014.
 * [Z/N/H/C] flags
 */
class freg(name : String) : sreg(name)
{
    var zero : Boolean
        get() = (get() and 0x80 != 0)
        set(z) = set( if (z) { get() or 0x80 } else { get() and 0x7F } )

    var sub : Boolean
        get() = (get() and 0x40 != 0)
        set(z) = set( if (z) { get() or 0x40 } else { get() and 0xBF } )

    var hcarry : Boolean
        get() = (get() and 0x20 != 0)
        set(z) = set( if (z) { get() or 0x20 } else { get() and 0xDF } )

    var carry : Boolean
        get() = (get() and 0b00010000 != 0)
        set(z) = set( if (z) { get() or 0x10 } else { get() and 0xEF } )

    fun clear()
    {
        data = 0
    }

    override fun toString() = "{FLAGS $name = [$zero|$sub|$hcarry|$carry]}"
}