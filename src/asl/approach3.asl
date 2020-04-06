// 3 & Beliefs & Individual & All & Time & Natural
z.
+z <- !g[ap, l_1].

+!g[ap,l_1]
	<- 	!update(b[ap(1000)])[ap];
			!update(c[ap(2000)])[ap];
			!update(d[ap(3000)])[ap];
			!g[rp,l_1].

+!g[ap,l_2]
	<- 	!update(b[ap(1000)])[ap];
			!update(c[ap(2000)])[ap];
			!update(e[ap(1000)])[ap];
			!g[rp,l_2].

+!g[ap, l_3]
	<- 	!g[rp, l_3].

// -!g[ap, l_3].

+!g[rp,l_1]: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g[rp,l_1] <- !g[ap,l_2].

+!g[rp,l_2]: b[ap(1000)] & c[ap(2000)] & e[ap(1000)]
	<-	.print("GOAL G2").

+!g[rp,l_2] <- !g[ap,l_3].

+!g[rp,l_3] <- .print("GOAL G5").

// -!g[rp,l_1] <- .print("Falhou").
// -!g[rp,l_2] <- .print("Falhou").
-!g[rp,l_3] <- .print("Falhou").

+?b[ap(T)]
	<-	.time(HH,MM,SS,MS);
			+b[ap(T),lu(HH,MM,SS,MS)];
			.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS,MS);
			+c[ap(T),lu(HH,MM,SS,MS)];
			.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").

+?e[ap(T)]
	<-	.time(HH,MM,SS,MS);
			+e[ap(T),lu(HH,MM,SS,MS)];
			.print("Active perception plan for e").

+!update(X)[ap]: not active_perception.isUpdated(X)
    <- ?X.

+!update(X)[ap].
