    ORG $1000

START:
    MOVE.L DISP+0,-(A7)
    LINK A6,#-40
    MOVE.L A6,DISP+0

    LEA S_BUF_0, A0
    MOVE.L A0,-8(A6)
    BRA.W E1
E2:
    MOVE.L DISP+4,-(A7)
    LINK A6,#-8
    MOVE.L A6,DISP+4

E3:
E4:
    MOVE.L #LC_0,-4(A6)
    MOVE.L -4(A6),A1
    MOVE.L #13,D0
    TRAP #15
    BRA.W E6
E5:
    MOVE.L #LC_1,-8(A6)
    MOVE.L -8(A6),A1
    MOVE.L #13,D0
    TRAP #15
E6:

    MOVE.L 4(A6),DISP+4
    UNLK A6
    ADD.L #4,A7
    RTS

E1:
    MOVE.L #LC_2,-16(A6)
    MOVE.L -16(A6),A1
    MOVE.L #13,D0
    TRAP #15
    MOVE.L #BTS,-20(A6)
    MOVE.L -20(A6),A1
    MOVE.L #2,D0
    TRAP #15
    MOVE.L -8(A6),A1
    MOVE.L -20(A6),A0
    JSR COPY_STRING
    MOVE.L #LC_3,-24(A6)
    MOVE.L -24(A6),A1
    MOVE.L #13,D0
    TRAP #15
    MOVE.L #4,D0
    TRAP #15
    MOVE.L D1,-28(A6)
    MOVE.L -28(A6),-4(A6)
    MOVE.L #18,-32(A6)
    MOVE.L -4(A6),D0
    MOVE.L -32(A6),D1
    CMP.L D1,D0
    BGE.W E7
    BRA.W E8
E7:
    MOVE.L #-1,-12(A6)
    BRA.W E9
E8:
    MOVE.L #0,-12(A6)
E9:
    MOVE.L #LC_4,-36(A6)
    MOVE.L -36(A6),A0
    MOVE.L #BTS,A1
    JSR COPY_STRING
    MOVE.L -8(A6),A0
    MOVE.L #BTS,A1
    JSR APPEND_STRING
    MOVE.L #BTS,-40(A6)
    MOVE.L -40(A6),A1
    MOVE.L #13,D0
    TRAP #15
    MOVE.L -12(A6),-(A7)
    JSR E2
    ADD.L #4,A7

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

LC_2: DC.B 'Introdueix el teu nom:',0
LC_1: DC.B 'Ets menor d''edat.',0
LC_0: DC.B 'Ets major d''edat.',0
LC_3: DC.B 'Introdueix la teva edat:',0
LC_4: DC.B 'Nom: ',0
S_BUF_0: DS.B 512

DISP: DS.L 2

HP: DS.L 1

BTS: DS.B 512

CERT_STR: DC.B 'cert',0
FALS_STR: DC.B 'fals',0

	END START