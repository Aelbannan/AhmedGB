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
            0xCB    -> extdecode()
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

    fun extdecode()
    {
        val opcode = mmu.nextb.get() and 0xff;
        println("-------------\n< EXT@ ${pc.get()-1} = $opcode > ")

        when (opcode)
        {
            // --------------------------------------------- //
            // RLC
            0x00    -> rlc          (b)
            0x01    -> rlc          (c)
            0x02    -> rlc          (d)
            0x03    -> rlc          (e)
            0x04    -> rlc          (h)
            0x05    -> rlc          (l)
            0x06    -> rlc          (mmu(hl))
            0x07    -> rlc          (b)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RRC
            0x08    -> rrc          (b)
            0x09    -> rrc          (c)
            0x0A    -> rrc          (d)
            0x0B    -> rrc          (e)
            0x0C    -> rrc          (h)
            0x0D    -> rrc          (l)
            0x0E    -> rrc          (mmu(hl))
            0x0F    -> rrc          (b)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RL
            0x10    -> rl           (b)
            0x11    -> rl           (c)
            0x12    -> rl           (d)
            0x13    -> rl           (e)
            0x14    -> rl           (h)
            0x15    -> rl           (l)
            0x16    -> rl           (mmu(hl))
            0x17    -> rl           (b)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RR
            0x18    -> rr           (b)
            0x19    -> rr           (c)
            0x1A    -> rr           (d)
            0x1B    -> rr           (e)
            0x1C    -> rr           (h)
            0x1D    -> rr           (l)
            0x1E    -> rr           (mmu(hl))
            0x1F    -> rr           (b)
            // --------------------------------------------- //


            // --------------------------------------------- //
            // SLA
            0x20    -> sla          (b)
            0x21    -> sla          (c)
            0x22    -> sla          (d)
            0x23    -> sla          (e)
            0x24    -> sla          (h)
            0x25    -> sla          (l)
            0x26    -> sla          (mmu(hl))
            0x27    -> sla          (b)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // SRA
            0x28    -> sra          (b)
            0x29    -> sra          (c)
            0x2A    -> sra          (d)
            0x2B    -> sra          (e)
            0x2C    -> sra          (h)
            0x2D    -> sra          (l)
            0x2E    -> sra          (mmu(hl))
            0x2F    -> sra          (b)
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // SRL
            0x38    -> srl          (b)
            0x39    -> srl          (c)
            0x3A    -> srl          (d)
            0x3B    -> srl          (e)
            0x3C    -> srl          (h)
            0x3D    -> srl          (l)
            0x3E    -> srl          (mmu(hl))
            0x3F    -> srl          (b)
            // --------------------------------------------- //


            // --------------------------------------------- //
            // SWAP
            0x30    -> swap         (b)
            0x31    -> swap         (c)
            0x32    -> swap         (d)
            0x33    -> swap         (e)
            0x34    -> swap         (h)
            0x35    -> swap         (l)
            0x36    -> swap         (mmu(hl))
            0x37    -> swap         (b)
            // --------------------------------------------- //


            // --------------------------------------------- //
            // BIT
            0x40    -> b            bit             0
            0x41    -> c            bit             0
            0x42    -> d            bit             0
            0x43    -> e            bit             0
            0x44    -> h            bit             0
            0x45    -> l            bit             0
            0x46    -> mmu(hl)      bit             0
            0x47    -> a            bit             0

            0x48    -> b            bit             1
            0x49    -> c            bit             1
            0x4A    -> d            bit             1
            0x4B    -> e            bit             1
            0x4C    -> h            bit             1
            0x4D    -> l            bit             1
            0x4E    -> mmu(hl)      bit             1
            0x4F    -> a            bit             1

            0x50    -> b            bit             2
            0x51    -> c            bit             2
            0x52    -> d            bit             2
            0x53    -> e            bit             2
            0x54    -> h            bit             2
            0x55    -> l            bit             2
            0x56    -> mmu(hl)      bit             2
            0x57    -> a            bit             2

            0x58    -> b            bit             3
            0x59    -> c            bit             3
            0x5A    -> d            bit             3
            0x5B    -> e            bit             3
            0x5C    -> h            bit             3
            0x5D    -> l            bit             3
            0x5E    -> mmu(hl)      bit             3
            0x5F    -> a            bit             3

            0x60    -> b            bit             4
            0x61    -> c            bit             4
            0x62    -> d            bit             4
            0x63    -> e            bit             4
            0x64    -> h            bit             4
            0x65    -> l            bit             4
            0x66    -> mmu(hl)      bit             4
            0x67    -> a            bit             4

            0x68    -> b            bit             5
            0x69    -> c            bit             5
            0x6A    -> d            bit             5
            0x6B    -> e            bit             5
            0x6C    -> h            bit             5
            0x6D    -> l            bit             5
            0x6E    -> mmu(hl)      bit             5
            0x6F    -> a            bit             5

            0x70    -> b            bit             6
            0x71    -> c            bit             6
            0x72    -> d            bit             6
            0x73    -> e            bit             6
            0x74    -> h            bit             6
            0x75    -> l            bit             6
            0x76    -> mmu(hl)      bit             6
            0x77    -> a            bit             6

            0x78    -> b            bit             7
            0x79    -> c            bit             7
            0x7A    -> d            bit             7
            0x7B    -> e            bit             7
            0x7C    -> h            bit             7
            0x7D    -> l            bit             7
            0x7E    -> mmu(hl)      bit             7
            0x7F    -> a            bit             7
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // RES
            0x80    -> b            res             0
            0x81    -> c            res             0
            0x82    -> d            res             0
            0x83    -> e            res             0
            0x84    -> h            res             0
            0x85    -> l            res             0
            0x86    -> mmu(hl)      res             0
            0x87    -> a            res             0

            0x88    -> b            res             1
            0x89    -> c            res             1
            0x8A    -> d            res             1
            0x8B    -> e            res             1
            0x8C    -> h            res             1
            0x8D    -> l            res             1
            0x8E    -> mmu(hl)      res             1
            0x8F    -> a            res             1

            0x90    -> b            res             2
            0x91    -> c            res             2
            0x92    -> d            res             2
            0x93    -> e            res             2
            0x94    -> h            res             2
            0x95    -> l            res             2
            0x96    -> mmu(hl)      res             2
            0x97    -> a            res             2

            0x98    -> b            res             3
            0x99    -> c            res             3
            0x9A    -> d            res             3
            0x9B    -> e            res             3
            0x9C    -> h            res             3
            0x9D    -> l            res             3
            0x9E    -> mmu(hl)      res             3
            0x9F    -> a            res             3

            0xA0    -> b            res             4
            0xA1    -> c            res             4
            0xA2    -> d            res             4
            0xA3    -> e            res             4
            0xA4    -> h            res             4
            0xA5    -> l            res             4
            0xA6    -> mmu(hl)      res             4
            0xA7    -> a            res             4

            0xA8    -> b            res             5
            0xA9    -> c            res             5
            0xAA    -> d            res             5
            0xAB    -> e            res             5
            0xAC    -> h            res             5
            0xAD    -> l            res             5
            0xAE    -> mmu(hl)      res             5
            0xAF    -> a            res             5

            0xB0    -> b            res             6
            0xB1    -> c            res             6
            0xB2    -> d            res             6
            0xB3    -> e            res             6
            0xB4    -> h            res             6
            0xB5    -> l            res             6
            0xB6    -> mmu(hl)      res             6
            0xB7    -> a            res             6

            0xB8    -> b            res             7
            0xB9    -> c            res             7
            0xBA    -> d            res             7
            0xBB    -> e            res             7
            0xBC    -> h            res             7
            0xBD    -> l            res             7
            0xBE    -> mmu(hl)      res             7
            0xBF    -> a            res             7
            //~~~~~~~~~~~~~~~~~~~~~~~~~~//
            // SET
            0xC0    -> b            set             0
            0xC1    -> c            set             0
            0xC2    -> d            set             0
            0xC3    -> e            set             0
            0xC4    -> h            set             0
            0xC5    -> l            set             0
            0xC6    -> mmu(hl)      set             0
            0xC7    -> a            set             0

            0xC8    -> b            set             1
            0xC9    -> c            set             1
            0xCA    -> d            set             1
            0xCB    -> e            set             1
            0xCC    -> h            set             1
            0xCD    -> l            set             1
            0xCE    -> mmu(hl)      set             1
            0xCF    -> a            set             1

            0xD0    -> b            set             2
            0xD1    -> c            set             2
            0xD2    -> d            set             2
            0xD3    -> e            set             2
            0xD4    -> h            set             2
            0xD5    -> l            set             2
            0xD6    -> mmu(hl)      set             2
            0xD7    -> a            set             2

            0xD8    -> b            set             3
            0xD9    -> c            set             3
            0xDA    -> d            set             3
            0xDB    -> e            set             3
            0xDC    -> h            set             3
            0xDD    -> l            set             3
            0xDE    -> mmu(hl)      set             3
            0xDF    -> a            set             3

            0xE0    -> b            set             4
            0xE1    -> c            set             4
            0xE2    -> d            set             4
            0xE3    -> e            set             4
            0xE4    -> h            set             4
            0xE5    -> l            set             4
            0xE6    -> mmu(hl)      set             4
            0xE7    -> a            set             4

            0xE8    -> b            set             5
            0xE9    -> c            set             5
            0xEA    -> d            set             5
            0xEB    -> e            set             5
            0xEC    -> h            set             5
            0xED    -> l            set             5
            0xEE    -> mmu(hl)      set             5
            0xEF    -> a            set             5

            0xF0    -> b            set             6
            0xF1    -> c            set             6
            0xF2    -> d            set             6
            0xF3    -> e            set             6
            0xF4    -> h            set             6
            0xF5    -> l            set             6
            0xF6    -> mmu(hl)      set             6
            0xF7    -> a            set             6

            0xF8    -> b            set             7
            0xF9    -> c            set             7
            0xFA    -> d            set             7
            0xFB    -> e            set             7
            0xFC    -> h            set             7
            0xFD    -> l            set             7
            0xFE    -> mmu(hl)      set             7
            0xFF    -> a            set             7
            // --------------------------------------------- //
        }
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

        val i = r.get() + 1

        f.zero = (i == 0)
        f.carry = (i and 0x10 != 0)
        f.sub = false

        r.set(i)

        println("DEBUG inc:$r")
    }

    // decrement
    private inline fun dec(r : register)
    {
        println("DEC $r")

        val i = r.get() - 1

        f.zero = (i == 0)
        f.carry = (i and 0x10 != 0)
        f.sub = true

        r.set(i)

        println("DEBUG dec:$r")
    }

    // add two registers
    private inline fun register.add(r : memory)
    {
        println("ADD dest:$this add:$r")
        val s = this.get() + r.get()

        f.clear()
        f.zero = (s == 0)
        f.hcarry = (s > 15)
        f.carry = (s > 0xff)

        this.set(s)

        println("DEBUG add:$this flags:$f")
    }

    // add two registers with carry
    private inline fun register.adc(r : memory)
    {
        println("ADC dest:$this add:$r")

        val s = this.get() + r.get() + (if (f.carry) 1 else 0)

        f.clear()
        f.zero = (s == 0)
        f.hcarry = (s > 15)
        f.carry = (s > 0xff)

        this.set(s)

        println("DEBUG adc:$this flags:$f")
    }

    // subtract two registers
    // TODO in gamboy logic 6-7=0
    private inline fun register.sub(r : memory)
    {
        println("SUB dest:$this sub:$r")

        val s = this.get() - r.get()

        f.clear()
        f.zero = (s == 0)
        f.carry = (s < 0)
        f.hcarry = (s < 16)
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
        f.zero = (s == 0)
        f.carry = (s < 0)
        f.hcarry = (s < 16)
        f.sub = true

        this.set(s)

        println("DEBUG sbc:$this flags:$f")
    }

    // compare two registers
    private inline fun register.cp(r : memory)
    {
        println("CMP a:$this b:$r")

        val s = this.get() - r.get()

        f.clear()
        f.zero = (s == 0)
        f.carry = (s < 0)
        f.hcarry = (s < 16)
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
        f.zero = (a == 0)
        f.hcarry = true

        this.set(a)
        println("DEBUG dest:$this")
    }

    // & two registers, store result in a
    public inline fun register.xor(r : memory)
    {
        println("XOR dest:$this src:$r")
        val a = this.get() xor r.get()

        f.clear()
        f.zero = (a == 0)

        this.set(a)
        println("DEBUG dest:$this")
    }

    // & two registers, store result in a
    public inline fun register.or(r : memory)
    {
        println("OR dest:$this src:$r")
        val a = this.get() or r.get()

        f.clear()
        f.zero = (a == 0)

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
    // SHIFTS

    // shift left with sign
    private inline fun sla(m : memory)
    {
        var s : Int = m.get() shl 1

        f.clear()
        f.carry = (s and 0x100 == 0)
        f.zero = (s and 0xff == 0)

        m.set(s)
    }

    // shift right with sign
    private inline fun sra(m : memory)
    {
        var s : Int = m.get()

        f.clear()
        f.carry = (s and 0x01 == 0)

        s = s ushr 1
        s += (s and 0x40) shl 1
        f.zero = (s and 0xff == 0)

        m.set(s)
    }

    // shift right with no sign
    private inline fun srl(m : memory)
    {
        var s : Int = m.get()

        f.clear()
        f.carry = (s and 0x01 == 0)

        s = s ushr 1
        f.zero = (s and 0xff == 0)

        m.set(s)
    }

    private inline fun swap(m : memory)
    {
        var sw = m.get()
        val hi = (sw and 0x0f) shl 4
        sw = sw shr 4 + hi

        f.clear()
        f.zero = (sw == 0)

        m.set(sw)
    }

    //************************************************************************************//
    //************************************************************************************//
    // BITS

    // test bit i
    private inline fun memory.bit(i : Int)
    {
        val b = this.get()

        f.zero = (b and (1 shl i) == 0)
        f.sub = false
        f.hcarry = true
    }

    // set bit i
    private inline fun memory.set(i : Int)
    {
        val b = 1 shl i
        this.set( this.get() or b )
    }

    // reset/clear bit i
    private inline fun memory.res(i : Int)
    {
        val b = 0xff xor (1 shl i)
        this.set( this.get() and b)
    }


    //************************************************************************************//
    //************************************************************************************//
    // ROTATES

    // rotate a left and leftmost bit to carry and 8th bits
    // TODO opitimize all rotates
    private inline fun rlc(m : memory)
    {
        println("RLC r:$m flags:$f")

        // get the register value and shift << 1
        var s = m.get() shl 1
        // clear flags
        f.clear()

        // move the 9th bit (8th before shift) to carry and 1st bit
        if (s and 0x100 != 0)
        {
            f.carry = true
            s += 1
        }

        // save to register
        m.set(s)

        println("DEBUG r:$m flags:$f")
    }

    // rotate right, rightmost bit to carry and 8th bit
    private inline fun rrc(m : memory)
    {
        println("RRC r:$m flags:$f")

        // get register a and add carry
        var s = m.get()
        // clear flags
        f.clear()

        // move the 1st bit to carry, 8th bit
        if (s and 1 != 0)
        {
            f.carry = true
            s += 0x100
        }

        // shift & save to register
        m.set(s shr 1)

        println("DEBUG r:$m flags:$f")
    }

    // rotate a left and leftmost bit to carry, carry to first bit
    private inline fun rl(m : memory)
    {
        println("RL r:$m flags:$f")

        // get the register value and shift << 1
        var s = m.get() shl 1
        s += if (f.carry) 1 else 0

        // clear flags
        f.clear()

        // move the 9th bit (8th before shift) to carry
        if (s and 0b100000000 != 0)
            f.carry = true

        // save to register
        m.set(s)

        println("DEBUG r:$m flags:$f")
    }

    // rotate right, rightmost bit to carry, carry to 8th bit
    private inline fun rr(m : memory)
    {
        println("RR m:$m flags:$f")

        // get register a and add carry
        var s = m.get()
        s += if (f.carry) 1 shl 8 else 0

        f.clear()

        // move the 1st bit to carry
        if (s and 1 != 0)
            f.carry = true

        // save to register
        m.set(s shr 1)

        println("DEBUG m:$m flags:$f")
    }

    // Logical NOT
    private inline fun cpl(r : register)
    {
        println("CPL r:$r")
        r.set(r.get() xor 0xff)
        println("DEBUG r:$r")
    }

}