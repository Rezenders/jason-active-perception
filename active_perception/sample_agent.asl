// Agent sample_agent in project active_perception

/* Initial beliefs and rules */
broken.
/* Initial goals */
!fly.

/* Plans */
+!fly: true
	<-	active_perception.reveal(allowed_to_fly, verify_status, 10000);
		.print("Takeoff").

+!fly: active_perception.reveal(allowed_to_fly, verify_status, 10000)
	<-	.print("Takeoff").

+!verify_status: not broken
	<-	.print("status");
		+allowed_to_fly.

+!verify_status: broken
	<-	-allowed_to_fly.
