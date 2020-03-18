
e.
// !g.
!start.
+e <- .wait(100);!g; .print("teste").
+j <- !g.

+!start <- .time(HH,MM,SS,MS); +b(1,2)[ap(1000),lu(HH,MM,SS,MS)].

+!g: b(1,2)[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g: b(2,Y)[ap(1000)] & c[ap(2000)]
	<-	.print("GOAL G2").

+!g: e & c[ap(2000)]
	<-	.print("GOAL G4").

+!g:  c[ap(2000)] & f
	<-	.print("GOAL G5").

+!g <- .print("GOAL G3").

-!g <- .print("Falhou").

+?b(X,Y)[ap(T)]
	<-	.time(HH,MM,SS,MS);
			+b(X,Y)[ap(T),lu(HH,MM,SS,MS)];
			.print("X: ", X, " Y: ", Y);
			.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+c[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").

{apply_ap}
