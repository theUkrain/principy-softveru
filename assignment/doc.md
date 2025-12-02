#Documentation for our design

Our design is different from initial one.

## Our effects

We have tried to implement all effects, that can be present in the game:
-Exchange allows player to trade-off one of possible inputs to one of possible outputs
-RawMaterialProducer produces one copy of resource given in constructor 
-Transformation fixed allows player to trade of fixed input to fixed output
-EffectOr represents Composite pattern on effect
-...

Effects as well process main game logic. Effects are managed with classes in "process" package.

##Card-effect correspondence

All  effects extend SetCardToEffect interface, witch guarantees, that effect, given to card, will stick to his card forever.
That robustness between cards and effects is provided via CardFactory. Card Factory also controls, that there will be produced 
at most 23 cards of type I and 24 cards of type II.

##Visitor pattern 

Our main difference was implementation of effect management via visitor pattern, where: 
-ProcessActionDeliver is "visitor"
-Effects are "components"

ProcessActionDeliver handles communication with player (via standard input stream), while each individual ProcessAction handles 
respected effect


 