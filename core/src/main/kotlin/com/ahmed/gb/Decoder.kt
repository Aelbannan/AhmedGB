package com.ahmed.gb

import com.ahmed.gb.memory.memory
import com.ahmed.gb.memory.registers.register
import com.ahmed.gb.memory.registers.dreg
import com.ahmed.gb.memory.registers.preg
import com.ahmed.gb.memory.registers.sreg
import com.ahmed.gb.components.MMU.mword
import com.ahmed.gb.memory.wordSized
import com.ahmed.gb.components.DMG
import com.ahmed.gb.components.MMU

/**
 * Created by Ahmed on 12/16/2014.
 * Runs opcodes via interpretation
 */
class Decoder(val cpu : DMG)
{
    val reg : DMG.registers
        get() = cpu.reg
    val clock: DMG.clock
        get() = cpu.clk
    val mmu : MMU
        get() = cpu.device.mmu


    val a = reg.a
    val b = reg.b
    val c = reg.c
    val d = reg.d
    val e = reg.e
    val f = reg.f
    val h = reg.h
    val l = reg.l

    val af = reg.af
    val bc = reg.bc
    val de = reg.de
    var hl = reg.hl

    val sp = reg.sp
    val pc = reg.pc


    // decodes functions from opcode
    fun decode()
    {
        // read opcode (byte) and pc++
        val opcode = mmu.nextb.get() and 0xff;
        println("-------------\n< PC ${pc.get()-1} = $opcode > ")
        //println(pc.toString() + " \t| opcode : $opcode \t|\t")

        when (opcode)
        {
            // --------------------------------------------- //
            // NOP & STOP & OTHER COOL FUNCTIONS
            0x00    -> nop()
            0x10    -> cpu.STOP = true
            0x27    -> nop() // TODO DAA()
            0x37    -> f.carry = true
            0x3F    -> f.carry = false
            0x76    -> println("HALT") // TODO HALT()
            0xCB    -> extdecode(opcode)
            0xF3    -> cpu.IME = false // TODO DI()
            0xFB    -> cpu.IME = true  // TODO EI()
            // --------------------------------------------- //

            // --------------------------------------------- //
            // LOAD!!!!!!
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // 8BIT <- 8BIT
            0x40    -> b            ld          b
            0x41    -> b            ld          c
            0x42    -> b            ld          d
            0x43    -> b            ld          e
            0x44    -> b            ld          h
            0x45    -> b            ld          l
            0x46    -> b            ld          mmu(hl)
            0x47    -> b            ld          a

            0x48    -> c            ld          b
            0x49    -> c            ld          c
            0x4A    -> c            ld          d
            0x4B    -> c            ld          e
            0x4C    -> c            ld          h
            0x4D    -> c            ld          l
            0x4E    -> c            ld          mmu(hl)
            0x4F    -> c            ld          a

            0x50    -> d            ld          b
            0x51    -> d            ld          c
            0x52    -> d            ld          d
            0x53    -> d            ld          e
            0x55    -> d            ld          h
            0x55    -> d            ld          l
            0x56    -> d            ld          mmu(hl)
            0x57    -> d            ld          a

            0x58    -> e            ld          b
            0x59    -> e            ld          c
            0x5A    -> e            ld          d
            0x5B    -> e            ld          e
            0x5C    -> e            ld          h
            0x5D    -> e            ld          l
            0x5E    -> e            ld          mmu(hl)
            0x5F    -> e            ld          a

            0x60    -> h            ld          b
            0x61    -> h            ld          c
            0x62    -> h            ld          d
            0x63    -> h            ld          e
            0x66    -> h            ld          h
            0x66    -> h            ld          l
            0x66    -> h            ld          mmu(hl)
            0x67    -> h            ld          a

            0x68    -> l            ld          b
            0x69    -> l            ld          c
            0x6A    -> l            ld          d
            0x6B    -> l            ld          e
            0x6e    -> l            ld          h
            0x6D    -> l            ld          l
            0x6E    -> l            ld          mmu(hl)
            0x6F    -> l            ld          a

            0x78    -> a            ld          b
            0x79    -> a            ld          c
            0x7A    -> a            ld          d
            0x7B    -> a            ld          e
            0x7C    -> a            ld          h
            0x7D    -> a            ld          l
            0x7E    -> a            ld          mmu(hl)
            0x7F    -> a            ld          a
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // 8BIT <- MEM
            0x0A    -> a            ld          mmu(bc)
            0x1A    -> a            ld          mmu(de)
            0x2A    -> a            ld          mmu(hl++)
            0x3A    -> a            ld          mmu(hl--)
            0xF2    -> a            ld          mmu(c)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // MEM <- 8BIT
            0x02    -> mmu(bc)      ld          a
            0x12    -> mmu(de)      ld          a
            0x22    -> mmu(hl++)    ld          a
            0x32    -> mmu(hl--)    ld          a
            0x70    -> mmu(hl)      ld          b
            0x71    -> mmu(hl)      ld          c
            0x72    -> mmu(hl)      ld          d
            0x73    -> mmu(hl)      ld          e
            0x74    -> mmu(hl)      ld          h
            0x75    -> mmu(hl)      ld          l
            0x77    -> mmu(hl)      ld          a
            0xE2    -> mmu(c)       ld          a
            0xEA    -> mmu.nextw    ld          a
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // 8BIT <- IMMEDIATE
            0x06    -> b            ld          mmu.nextb
            0x0E    -> c            ld          mmu.nextb
            0x16    -> d            ld          mmu.nextb
            0x1E    -> e            ld          mmu.nextb
            0x26    -> h            ld          mmu.nextb
            0x2E    -> l            ld          mmu.nextb
            0x3E    -> a            ld          mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // ZERO PAGE
            0xE0    -> mmu(mmu.nextb)   ld      a
            0xF0    -> a            ld          mmu(mmu.nextb)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // 16BIT
            0x01    -> bc           ld          mmu.nextw
            0x08    -> mmu.nexta    ld          sp
            0x11    -> de           ld          mmu.nextw
            0x21    -> hl           ld          mmu.nextw
            0x31    -> sp           ld          mmu.nextw
            0xF9    -> sp           ld          hl
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // MEM <- IMMEDIATE
            0x36    -> mmu(hl)      ld          mmu.nextb
            // --------------------------------------------- //


            // --------------------------------------------- //
            // ROTATES & SHIFTS
            0x07    -> rlc          (a)
            0x0F    -> rrc          (a)
            0x17    -> rl           (a)
            0x1F    -> rr           (a)
            // --------------------------------------------- //


            // --------------------------------------------- //
            // JP
            0xC3    -> mmu.nextw    jp      true
            0xC2    -> mmu.nextw    jp      !f.zero
            0xCA    -> mmu.nextw    jp      f.zero
            0xD2    -> mmu.nextw    jp      !f.carry
            0xDA    -> mmu.nextw    jp      f.carry
            0xE9    -> hl           jp      true
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // JR
            0x18    -> jr           (true)
            0x20    -> jr           (!f.zero)
            0x28    -> jr           (f.zero)
            0x30    -> jr           (!f.carry)
            0x38    -> jr           (f.carry)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // CALL
            0xCD    -> mmu.nextw    call    true
            0xC4    -> mmu.nextw    call    !f.zero
            0xCC    -> mmu.nextw    call    f.zero
            0xD4    -> mmu.nextw    call    !f.carry
            0xDC    -> mmu.nextw    call    f.carry
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RST
            0xC7    ->  rst         (0x00)
            0xCF    ->  rst         (0x08)
            0xD7    ->  rst         (0x10)
            0xDF    ->  rst         (0x18)
            0xE7    ->  rst         (0x20)
            0xEF    ->  rst         (0x28)
            0xF7    ->  rst         (0x30)
            0xFF    ->  rst         (0x38)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RET
            0xC9    -> ret          (true)
            0xC0    -> ret          (!f.zero)
            0xC8    -> ret          (f.zero)
            0xD0    -> ret          (!f.carry)
            0xD8    -> ret          (f.carry)
            // RETI
            0xD9    -> ret          (true) // TODO RETI()
            // --------------------------------------------- //


            // --------------------------------------------- //
            // INCREMENT & DECREMENT
            0x03    -> inc          (bc)
            0x04    -> inc          (b)
            0x0B    -> inc          (bc)
            0x0C    -> inc          (c)
            0x13    -> inc          (de)
            0x14    -> inc          (d)
            0x1C    -> inc          (e)
            0x23    -> inc          (hl)
            0x24    -> inc          (h)
            0x2C    -> inc          (l)
            0x33    -> inc          (sp)
            0x34    -> mmu.inc      (hl)
            0x3C    -> inc          (a)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            0x05    -> dec          (b)
            0x0D    -> dec          (c)
            0x15    -> dec          (d)
            0x1B    -> dec          (de)
            0x1D    -> dec          (e)
            0x25    -> dec          (h)
            0x2B    -> dec          (hl)
            0x2D    -> dec          (l)
            0x35    -> mmu.dec      (hl)
            0x3B    -> dec          (sp)
            0x3D    -> dec          (a)
            // --------------------------------------------- //

            // --------------------------------------------- //
            // ADD
            0x09    -> hl           add         bc
            0x19    -> hl           add         de
            0x29    -> hl           add         hl
            0x39    -> hl           add         sp

            0xC6    -> a            add         mmu.nextb

            0x80    -> a            add         b
            0x81    -> a            add         c
            0x82    -> a            add         d
            0x83    -> a            add         e
            0x84    -> a            add         h
            0x85    -> a            add         l
            0x86    -> a            add         mmu(hl)
            0x87    -> a            add         a
            0xC6    -> a            add         mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // ADC
            0x88    -> a            adc         b
            0x89    -> a            adc         c
            0x8A    -> a            adc         d
            0x8B    -> a            adc         e
            0x8C    -> a            adc         h
            0x8D    -> a            adc         l
            0x8E    -> a            adc         mmu(hl)
            0x8F    -> a            adc         a
            0xCE    -> a            adc         mmu.nextb
            // --------------------------------------------- //


            // --------------------------------------------- //
            // SUB
            0x90    -> a            sub         b
            0x91    -> a            sub         c
            0x92    -> a            sub         d
            0x93    -> a            sub         e
            0x94    -> a            sub         h
            0x95    -> a            sub         l
            0x96    -> a            sub         mmu(hl)
            0x97    -> a            sub         a
            0xD6    -> a            sub         mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // SBC
            0x98    -> a            sbc         b
            0x99    -> a            sbc         c
            0x9A    -> a            sbc         d
            0x9B    -> a            sbc         e
            0x9C    -> a            sbc         h
            0x9D    -> a            sbc         l
            0x9E    -> a            sbc         mmu(hl)
            0x9F    -> a            sbc         a
            0xDE    -> a            sbc         mmu.nextb
            // --------------------------------------------- //


            // --------------------------------------------- //
            // LOGICAL FUN...
            0x2F    -> cpl          (a)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // AND
            0xA0    -> a            and         b
            0xA1    -> a            and         c
            0xA2    -> a            and         d
            0xA3    -> a            and         e
            0xA4    -> a            and         h
            0xA5    -> a            and         l
            0xA6    -> a            and         mmu(hl)
            0xA7    -> a            and         a
            0xE6    -> a            and         mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // XOR
            0xA8    -> a            xor         b
            0xA9    -> a            xor         c
            0xAA    -> a            xor         d
            0xAB    -> a            xor         e
            0xAC    -> a            xor         h
            0xAD    -> a            xor         l
            0xAE    -> a            xor         mmu(hl)
            0xAF    -> a            xor         a
            0xEE    -> a            xor         mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // OR
            0xB0    -> a            or          b
            0xB1    -> a            or          c
            0xB2    -> a            or          d
            0xB3    -> a            or          e
            0xB4    -> a            or          h
            0xB5    -> a            or          l
            0xB6    -> a            or          mmu(hl)
            0xB7    -> a            or          a
            0xF6    -> a            or          mmu.nextb
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // CP
            0xB8    -> a            cp          b
            0xB9    -> a            cp          c
            0xBB    -> a            cp          d
            0xBB    -> a            cp          e
            0xBC    -> a            cp          h
            0xBD    -> a            cp          l
            0xBE    -> a            cp          mmu(hl)
            0xBF    -> a            cp          a
            0xFE    -> a            cp          mmu.nextb
            // --------------------------------------------- //


            // --------------------------------------------- //
            // POP
            0xF1    -> pop          (af)
            0xC1    -> pop          (bc)
            0xD1    -> pop          (de)
            0xE1    -> pop          (hl)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // PUSH
            0xF5    -> push         (af)
            0xC5    -> push         (bc)
            0xD5    -> push         (de)
            0xE5    -> push         (hl)
            // --------------------------------------------- //
            else    -> println("UNIMPLEMENTED FUNCTION")
        }
    }

    fun extdecode(opcode : Int)
    {

    }

    // do nothing
    private inline fun nop() = println("nop")

    //************************************************************************************//
    //************************************************************************************//
    // MEMORY

    // NEW & IMPROVED load function, does it all!
    private inline fun memory.ld(src : memory)
    {
        println("LOAD dest:$this src:$src")
        this.set(src.get())
        println("DEBUG dest:$this")
    }

    private inline fun push(m : memory)
    {
        sp -= 2
        println("PUSH mem:$m sp:$sp")
        m.get().push()

    }

    private inline fun Int.push()
    {
        sp -= 2
        mmu.ww(sp.get(), this and 0xffff)
        println("DEBUG stack:${mmu.rw(sp.get())}")
    }

    public inline fun pop(m : memory)
    {
        println("POP sp:$sp")
        m.set(popi())
    }

    private inline fun popi() : Int
    {
        val p = mmu.rw(sp.get()) and 0xffff
        sp += 2
        println("DEBUG int:$p; stack:${mmu.rw(sp.get()-2)}")
        return p
    }

    //************************************************************************************//
    //************************************************************************************//
    // MATH

    // increment
    private inline fun inc( r : register)
    {
        println("INC $r")
        r.inc()
        println("DEBUG inc:$r")
    }

    // decrement
    private inline fun dec(r : register)
    {
        println("DEC $r")
        r.dec()
        println("DEBUG dec:$r")
    }

    // add two registers
    private inline fun register.add(r : memory)
    {
        println("ADD dest:$this add:$r")

        f.clear()

        val s = this.get() + r.get()

        if (s == 0) f.zero = true
        if (s > 15) f.hcarry = true
        if (s > 0xff) f.carry = true

        this.set(s)

        println("DEBUG add:$this flags:$f")
    }

    // add two registers with carry
    private inline fun register.adc(r : memory)
    {
        println("ADC dest:$this add:$r")

        val s = this.get() + r.get() + (if (f.carry) 1 else 0)

        f.clear()

        if (s == 0) f.zero = true
        if (s > 15) f.hcarry = true
        if (s > 0xff) f.carry = true

        this.set(s)

        println("DEBUG adc:$this flags:$f")
    }

    // subtract two registers
    private inline fun register.sub(r : memory)
    {
        println("SUB dest:$this sub:$r")

        f.clear()

        val s = this.get() - r.get()

        if (s == 0) f.zero = true
        if (s < 0) f.carry = true
        f.sub = true

        this.set(s)

        println("DEBUG sub:$this flags:$f")
    }

    // subtract two registers with carry
    private inline fun register.sbc(r : memory)
    {
        println("SBC dest:$this sbc:$r")

        val s = this.get() - r.get() - (if (f.carry) 1 else 0)

        f.clear()

        if (s == 0) f.zero = true
        if (s < 0) f.carry = true
        f.sub = true

        this.set(s)

        println("DEBUG sbc:$this flags:$f")
    }

    // compare two registers
    private inline fun register.cp(r : memory)
    {
        println("CMP a:$this b:$r")

        f.clear()

        val s = this.get() - r.get()

        if (s == 0) f.zero = true
        if (s < 0) f.carry = true
        f.sub = true

        println("DEBUG flags:$f")
    }

    // Decimal adjust, not too sure how this works
    private inline fun da(r : register)
    {
        var d = r.get()

        if (f.sub)
        {
            if (d / 16 > 9)
                d -= 0x60

            if (d / 15 > 9)
                d -= 0x06
        }
        else
        {
            if (d / 16 > 9)
                d += 0x60

            if (d / 15 > 9)
                d += 0x06
        }

        f.carry = (d != (d and 0xff))
        f.zero = d == 0
        f.hcarry = false

        r.set(d)
    }

    //************************************************************************************//
    //************************************************************************************//
    // LOGIC

    // & two registers, store result in a
    public inline fun register.and(r : memory)
    {
        println("AND dest:$this src:$r")
        val a = this.get() and r.get()

        f.clear()
        if (a == 0) f.zero = true

        this.set(a)
        println("DEBUG dest:$this")
    }

    // & two registers, store result in a
    public inline fun register.xor(r : memory)
    {
        println("XOR dest:$this src:$r")
        val a = this.get() xor r.get()

        f.clear()
        if (a == 0) f.zero = true

        this.set(a)
        println("DEBUG dest:$this")
    }

    // & two registers, store result in a
    public inline fun register.or(r : memory)
    {
        println("OR dest:$this src:$r")
        val a = this.get() or r.get()

        f.clear()
        if (a == 0) f.zero = true

        this.set(a)
        println("DEBUG dest:$this")
    }


    //************************************************************************************//
    //************************************************************************************//
    // JUMPS & STUFF

    // jump relative
    // TODO maybe set to jump back $jump - 2 (cause we load op+byte)
    private inline fun jr(test : Boolean)
    {
        println("JR pc:$pc test:$test")

        if (test)
        {
            val jump = mmu.nextb.get().toByte()
            pc += jump.toInt() - 2

            println("DEBUG pc:$pc jump:$jump")
        }
    }

    // jump to address
    private inline fun memory.jp(test : Boolean)
    {
        println("JP pc:$pc addr:$this test:$test")
        if (test) pc.set(this.get())
        println("DEBUG pc:$pc")
    }

    // call subroutine
    private inline fun memory.call(test : Boolean)
    {
        println("CALL pc:$pc addr:$this test:$test")
        if (test)
        {
            push(pc)
            pc.set(this.get())
        }

        println("DEBUG pc:$pc call:$this")
    }

    // restart at line #
    private inline fun rst(addr : Int)
    {
        println("RST pc:$pc addr:$addr")
        val old = pc.get() - 1
        old.push()
        pc.set(addr)
        println("DEBUG pc:$pc")
    }

    // call subroutine
    private inline fun ret(test : Boolean)
    {
        println("RET pc:$pc test:$test")
        if (test) pc.set(popi())
        println("DEBUG pc:$pc")
    }

    //************************************************************************************//
    //************************************************************************************//
    // ROTATES

    // rotate a left and leftmost bit to carry and 8th bits
    // TODO opitimize all rotates
    private inline fun rlc(r : register)
    {
        println("RLC r:$r flags:$f")

        // get the register value and shift << 1
        var s = r.get() shl 1
        // clear flags
        f.clear()

        // move the 9th bit (8th before shift) to carry and 1st bit
        if (s and 0x100 != 0)
        {
            f.carry = true
            s += 1
        }

        // save to register
        r.set(s)

        println("DEBUG r:$r flags:$f")
    }

    // rotate right, rightmost bit to carry and 8th bit
    private inline fun rrc(r : register)
    {
        println("RRC r:$r flags:$f")

        // get register a and add carry
        var s = r.get()
        // clear flags
        f.clear()

        // move the 1st bit to carry, 8th bit
        if (s and 1 != 0)
        {
            f.carry = true
            s += 0x100
        }

        // shift & save to register
        r.set(s shr 1)

        println("DEBUG r:$r flags:$f")
    }

    // rotate a left and leftmost bit to carry, carry to first bit
    private inline fun rl(r : register)
    {
        println("RL r:$r flags:$f")

        // get the register value and shift << 1
        var s = r.get() shl 1
        s += if (f.carry) 1 else 0

        // clear flags
        f.clear()

        // move the 9th bit (8th before shift) to carry
        if (s and 0b100000000 != 0)
            f.carry = true

        // save to register
        r.set(s)

        println("DEBUG r:$r flags:$f")
    }

    // rotate right, rightmost bit to carry, carry to 8th bit
    private inline fun rr(r : register)
    {
        println("RR r:$r flags:$f")

        // get register a and add carry
        var s = r.get()
        s += if (f.carry) 1 shl 8 else 0

        f.clear()

        // move the 1st bit to carry
        if (s and 1 != 0)
            f.carry = true

        // save to register
        r.set(s shr 1)

        println("DEBUG r:$r flags:$f")
    }

    // Logical NOT
    private inline fun cpl(r : register)
    {
        println("CPL r:$r")
        r.set(r.get() xor 0xff)
        println("DEBUG r:$r")
    }

}