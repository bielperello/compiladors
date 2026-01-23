    ORG $1000

START:
    MOVE.L DISP+0,-(A7)
    LINK A6,#-72
    MOVE.L A6,DISP+0

    LEA S_BUF_0, A0
    MOVE.L A0,-24(A6)
    BRA.W E1
E2:
    MOVE.L DISP+4,-(A7)
    LINK A6,#-24
    MOVE.L A6,DISP+4

E3:
    MOVE.L #70,-8(A6)
    MOVE.L 12(A6),D0
    MOVE.L -8(A6),D1
    CMP.L D1,D0
    BLE.W E5
E4:
    MOVE.L #0,-12(A6)
    MOVE.L 16(A6),D0
    MOVE.L -12(A6),D1
    CMP.L D1,D0
    BNE.W E6
E5:
    MOVE.L #90,-16(A6)
    MOVE.L 12(A6),D0
    MOVE.L -16(A6),D1
    CMP.L D1,D0
    BLE.W E7
E6:
    MOVE.L #2,-4(A6)
    BRA.W E8
E7:
    MOVE.L #1,-4(A6)
E8:
    MOVE.L -4(A6),D0

    MOVE.L 4(A6),DISP+4
    UNLK A6
    ADD.L #4,A7
    RTS

E1:
    MOVE.L #LC_0,-16(A6)
    MOVE.L -16(A6),D0
    MOVE.L D0,0-16(A6)
    MOVE.L #70,-20(A6)
    MOVE.L -20(A6),D0
    MOVE.L D0,4-16(A6)
    MOVE.L #-1,-24(A6)
    MOVE.L -24(A6),D0
    MOVE.L D0,8-16(A6)
    MOVE.L #83,-28(A6)
    MOVE.L -28(A6),D0
    MOVE.L D0,12-16(A6)
    MOVE.L 8-16(A6),D0
    MOVE.L D0,-32(A6)
    MOVE.L -32(A6),-(A7)
    MOVE.L 4-16(A6),D0
    MOVE.L D0,-36(A6)
    MOVE.L -36(A6),-(A7)
    JSR E2
    ADD.L #8,A7
    MOVE.L D0,-40(A6)
    MOVE.L -40(A6),-20(A6)
    MOVE.L 0-16(A6),D0
    MOVE.L D0,-44(A6)
    MOVE.L -24(A6),A1
    MOVE.L -44(A6),A0
    JSR COPY_STRING
    MOVE.L #1,-48(A6)
    MOVE.L -20(A6),D0
    MOVE.L -48(A6),D1
    CMP.L D1,D0
    BNE.W E10
    MOVE.L #LC_1,-52(A6)
    MOVE.L -52(A6),A0
    MOVE.L #BTS,A1
    JSR COPY_STRING
    MOVE.L -24(A6),A0
    MOVE.L #BTS,A1
    JSR APPEND_STRING
    MOVE.L #BTS,-56(A6)
    MOVE.L -56(A6),A1
    MOVE.L #13,D0
    TRAP #15
    BRA.W E9
E10:
    MOVE.L #2,-60(A6)
    MOVE.L -20(A6),D0
    MOVE.L -60(A6),D1
    CMP.L D1,D0
    BNE.W E11
    MOVE.L #LC_2,-64(A6)
    MOVE.L -64(A6),A0
    MOVE.L #BTS,A1
    JSR COPY_STRING
    MOVE.L -24(A6),A0
    MOVE.L #BTS,A1
    JSR APPEND_STRING
    MOVE.L #BTS,-68(A6)
    MOVE.L -68(A6),A1
    MOVE.L #13,D0
    TRAP #15
    BRA.W E9
E11:
    MOVE.L #LC_3,-72(A6)
    MOVE.L -72(A6),A1
    MOVE.L #13,D0
    TRAP #15
E9:

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

LC_1: DC.B 'Estat normal del sensor ',0
LC_2: DC.B 'ALERTA: temperatura elevada del sensor ',0
LC_3: DC.B 'Estat desconegut',0
LC_0: DC.B 'Sensor Central',0
S_BUF_0: DS.B 512

DISP: DS.L 2

HP: DS.L 1

BTS: DS.B 512

CERT_STR: DC.B 'cert',0
FALS_STR: DC.B 'fals',0

	END START