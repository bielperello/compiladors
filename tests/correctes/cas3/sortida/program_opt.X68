    ORG $1000

START:
    MOVE.L DISP+0,-(A7)
    LINK A6,#-80
    MOVE.L A6,DISP+0

E1:
    MOVE.L #LC_0,-20(A6)
    MOVE.L -20(A6),A1
    MOVE.L #13,D0
    TRAP #15
    MOVE.L #4,D0
    TRAP #15
    MOVE.L D1,-24(A6)
    MOVE.L -24(A6),-4(A6)
    MOVE.L #-1,-12(A6)
    MOVE.L #2,-8(A6)
E2:
    MOVE.L #2,-36(A6)
    MOVE.L -4(A6),D0
    MOVE.L -36(A6),D1
    DIVS D1,D0
    MOVE.W D0,D0
    EXT.L D0
    MOVE.L D0,-40(A6)
    MOVE.L -8(A6),D0
    MOVE.L -40(A6),D1
    CMP.L D1,D0
    BGT.W E4
E5:
    MOVE.L #0,-44(A6)
    MOVE.L -12(A6),D0
    MOVE.L -44(A6),D1
    CMP.L D1,D0
    BEQ.W E4
E3:
    MOVE.L -4(A6),D0
    MOVE.L -8(A6),D1
    DIVS D1,D0
    MOVE.W D0,D0
    EXT.L D0
    MOVE.L D0,-48(A6)
    MOVE.L -48(A6),D0
    MOVE.L -8(A6),D1
    MULS D1,D0
    MOVE.L D0,-52(A6)
    MOVE.L -52(A6),-16(A6)
    MOVE.L -16(A6),D0
    MOVE.L -4(A6),D1
    CMP.L D1,D0
    BNE.W E7
E6:
    MOVE.L #0,-12(A6)
    BRA.W E8
E7:
    MOVE.L #1,-60(A6)
    MOVE.L -8(A6),D0
    MOVE.L -60(A6),D1
    ADD.L D1,D0
    MOVE.L D0,-64(A6)
    MOVE.L -64(A6),-8(A6)
E8:
    BRA.W E2
E4:
    MOVE.L #0,-68(A6)
    MOVE.L -12(A6),D0
    MOVE.L -68(A6),D1
    CMP.L D1,D0
    BEQ.W E11
E9:
    MOVE.L #1,-72(A6)
    MOVE.L -4(A6),D0
    MOVE.L -72(A6),D1
    CMP.L D1,D0
    BEQ.W E11
E10:
    MOVE.L #LC_1,-76(A6)
    MOVE.L -76(A6),A1
    MOVE.L #13,D0
    TRAP #15
    BRA.W E12
E11:
    MOVE.L #LC_2,-80(A6)
    MOVE.L -80(A6),A1
    MOVE.L #13,D0
    TRAP #15
E12:

    SIMHALT

* A0 = origen, A1 = desti
COPY_STRING:
.copy:
    MOVE.B (A0)+,D0
    MOVE.B D0,(A1)+
    BEQ .done
    BRA .copy
.done:
    RTS


* A0 = origen, A1 = desti
APPEND_STRING:
.trobar_final:
    MOVE.B (A1)+,D0
    BNE .trobar_final
    SUBA.L #1, A1
.append:
    MOVE.B (A0)+,D0
    MOVE.B D0,(A1)+
    BNE .append
    RTS

LC_1: DC.B 'El nombre es primer',0
LC_2: DC.B 'El nombre no es primer',0
LC_0: DC.B 'Introdueix un nombre enter major que 1:',0

DISP: DS.L 1

HP: DS.L 1

BTS: DS.B 512

CERT_STR: DC.B 'cert',0
FALS_STR: DC.B 'fals',0

	END START