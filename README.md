## Progetto del Corso di Laboratorio di Linguaggi: HaveFun

**Struttura dei Programmi.** Un programma avra' la seguente struttura:

    FUN1...FUNn COM

ovvero:
 1.  una sequenza aribratriamente lunga e potenzialmente vuota di definizioni di funzione
 2. seguite da un singolo comando (non opzionale)

**Definizione di Funzione.** Una definizione di funzione ha la seguente sintassi:

 1. sono ammesse funzioni con zero parametri
 2. il corpo della funzione e' opzionale
 3. il *return* deve comparire obbligatoriamente e solo come ultimo statement durante la definizione di una funzione
 4. non sono ammesse funzioni che non ritornano nessun valore
 
**Chiamata di Funzione.** La chiamata di una funzione sara' un'espressione con la seguente sintassi:
*id(EXP1, ... , EXPn)*

**Variabili globali.**

 1. Necessitano della keyword ***global*** nella dichiarazione
	 - ... **global x = 50;** ...
2. Le funzioni hanno modo di accedere ad esse e di modificarne il valore
3. In caso di utilizzo di una variabili global e' necessario aggiungere ***.g*** all'identificatore della variabile
	- ... **x.g + 10;** ...

**Programmi ArnoldC**
Possono essere inseriti sia nel corpo delle funzioni, che nel corpo principale del programma, utilizzando questo "costrutto":

    ${ ArnoldC-Programs }$
    
**Lista dei costrutti implementati:**

- IT'S SHOWTIME --- YOU HAVE BEEN TERMINATED
- TALK TO THE HAND
- HEY CHRISTMAS TREE --- YOU SET US UP
- GET TO THE CHOPPER --- HERE IS MY INVITATION --- ENOUGH TALK
- GET UP
- GET DOWN
- YOU'RE FIREDHE HAD TO SPLIT
- YOU ARE NOT YOU YOU ARE ME
- LET OFF SOME STEAM BENNET
- CONSIDER THAT A DIVORCE
- KNOCK KNOCK
- @I LIED --- @NO PROBLEMO
- BECAUSE I'M GOING TO SAY PLEASE --- BULLSHIT --- YOU HAVE NO RESPECT FOR LOGIC
- STICK AROUND --- CHILL

*Per la semantica consultare:*
https://github.com/lhartikk/ArnoldC/wiki/ArnoldC
