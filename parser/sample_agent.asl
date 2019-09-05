// Agent sample_agent in project ts_ap

/* Initial beliefs and rules */
e.
/* Initial goals */
+e <- !g.

/* Plans */
+!g <- !update_ap_g; !g_ap.

+!update(U) <- active_perception.update(U).

+!update_ap_g
	<- 	!update(b[ap(1000)]) |&|
		!update(c[ap(2000)]) |&|
		!update(d[ap(3000)]).

+!g_ap: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<- .print("GOAL G1").

+!g_ap: b[ap(1000)] & c[ap(2000)]
	<- .print("GOAL G2").

+!g_ap: b[ap(1000)]
	<- .print("GOAL G3").

+!g_ap <- .print("GOAL G4!").

+?b[ap(T)]
	<-	.time(HH,MM,SS);
		+b[ap(T),lu(HH,MM,SS)];
		.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS);
		+c[ap(T),lu(HH,MM,SS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").

{apply_ap("oi!!")}
