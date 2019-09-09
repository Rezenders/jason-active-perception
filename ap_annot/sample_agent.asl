// Agent sample_agent in project ap_annot

/* Initial beliefs and rules */
teste["ap"].
/* Initial goals */

!start.

/* Plans */

+!start : teste["ap","custom_perception_plan"]
	<- .print("hello world.").

+!active_perception_plan: true
	<- .print("Default active perception plan activated!").

+!custom_perception_plan: true
	<- 	+teste["ap","custom_perception_plan"];
		.print("Custom active perception plan activated!").
