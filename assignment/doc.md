# Documentation for our design

Our design differs from the initial one.

## Our effects

We have attempted to implement all effects that can be present in the game:

- Exchange allows the player to trade one of the possible inputs for one of the possible outputs.

- RawMaterialProducer produces one unit of the resource given in the constructor.

- TransformationFixed allows the player to trade a fixed input for a fixed output.

- EffectOr represents the Composite pattern applied to effects.

- …

Effects also process the main game logic. They are managed by the classes in the process package.

## Card–effect correspondence

All effects implement the SetCardToEffect interface, which guarantees that the effect assigned to a card will stay attached to that card permanently.
This robustness between cards and effects is ensured via the CardFactory.
CardFactory also controls that at most 23 cards of type I and 24 cards of type II can be produced.

## Visitor pattern

Our main difference lies in the implementation of effect management via the Visitor pattern, where:

ProcessActionDeliver is the visitor,

Effects act as the components.

ProcessActionDeliver handles communication with the player (via the standard input stream), while each individual ProcessAction handles its respective effect.