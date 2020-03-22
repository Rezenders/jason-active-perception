
e.
z.
// !g.
// !start.
!g(3,4).
// !g(3,4).
// +z <- .time(HH,MM,SS,MS); +k[ap(1000),lu(HH,MM,SS,MS)];!teste.
+e <- .wait(100); !g(3,4); .print("teste").

// +!start <- .time(HH,MM,SS,MS); +b(1,2)[ap(1000),lu(HH,MM,SS,MS)].

+!z(X,Y): b(1,2)[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g(Z,W): b(1,2)[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g(Z,W): b(2,Y)[ap(1000)] & c[ap(2000)]
	<-	.print("GOAL G2").

+!g(Z,W): e & c[ap(2000)]
	<-	.print("GOAL G4").

+!g(Z,W):  c[ap(2000)] & f
	<-	.print("GOAL G5").

+!g(Z,W) <- .print("GOAL G3").

-!g(Z,W) <- .print("Falhou").

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

// +!teste: active_perception.isUpdated(k[ap(1000)])
// 	<- .print("deve printar").

{apply_ap}
