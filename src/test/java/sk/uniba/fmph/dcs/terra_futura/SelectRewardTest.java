package sk.uniba.fmph.dcs.terra_futura;

public class SelectRewardTest {
/*
    private Card createMockCard() {
        // Replace with actual Card creation when Card class is implemented
        // Card should accept resources
        return null; // Replace with: new Card(...)
    }

    @Test
    public void testInitialState() {
        SelectReward selectReward = new SelectReward();

        assertFalse("Should have no player initially",
                selectReward.getPlayer().isPresent());
        assertFalse("Should have no remaining rewards",
                selectReward.hasRemainingRewards());
    }

    @Test
    public void testSetReward() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);

        selectReward.setReward(1, card, rewards);

        assertTrue("Player should be set", selectReward.getPlayer().isPresent());
        assertEquals("Player should be 1", 1, (int) selectReward.getPlayer().get());
        assertTrue("Should have remaining rewards",
                selectReward.hasRemainingRewards());
        assertEquals("Should have 2 available rewards",
                2, selectReward.getAvailableRewards().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRewardNullCard() {
        SelectReward selectReward = new SelectReward();
        List<Resource> rewards = List.of(Resource.Green);

        selectReward.setReward(1, null, rewards);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRewardEmptyRewards() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();

        selectReward.setReward(1, card, List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRewardNullRewards() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();

        selectReward.setReward(1, card, null);
    }

    @Test
    public void testCanSelectReward() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);

        selectReward.setReward(1, card, rewards);

        assertTrue("Should be able to select Green",
                selectReward.canSelectReward(Resource.Green));
        assertTrue("Should be able to select Red",
                selectReward.canSelectReward(Resource.Red));
        assertFalse("Should not be able to select Yellow",
                selectReward.canSelectReward(Resource.Yellow));
    }

    @Test
    public void testCanSelectRewardNoSetup() {
        SelectReward selectReward = new SelectReward();

        assertFalse("Should not be able to select without setup",
                selectReward.canSelectReward(Resource.Green));
    }

    @Test
    public void testSelectReward() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);

        selectReward.setReward(1, card, rewards);

        // This will fail until Card class is implemented with proper canPutResources
        // assertTrue("Should successfully select Green",
        //           selectReward.selectReward(Resource.Green));

        // After selection, should not be able to select same reward again
        // assertFalse("Should not be able to select Green again",
        //            selectReward.canSelectReward(Resource.Green));
    }

    @Test
    public void testSelectRewardInvalid() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green);

        selectReward.setReward(1, card, rewards);

        assertFalse("Should not select invalid reward",
                selectReward.selectReward(Resource.Yellow));
    }

    @Test
    public void testSelectAllRewards() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);

        selectReward.setReward(1, card, rewards);

        assertTrue("Should have remaining rewards",
                selectReward.hasRemainingRewards());

        // After selecting all rewards, state should clear
        // This will work once Card class is implemented
        // selectReward.selectReward(Resource.Green);
        // assertTrue("Should still have remaining",
        //           selectReward.hasRemainingRewards());

        // selectReward.selectReward(Resource.Red);
        // assertFalse("Should have no remaining",
        //            selectReward.hasRemainingRewards());
        // assertFalse("Player should be cleared",
        //            selectReward.getPlayer().isPresent());
    }

    @Test
    public void testIsPlayerTurn() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green);

        selectReward.setReward(1, card, rewards);

        assertTrue("Should be player 1's turn", selectReward.isPlayerTurn(1));
        assertFalse("Should not be player 2's turn", selectReward.isPlayerTurn(2));
    }

    @Test
    public void testIsPlayerTurnNoSetup() {
        SelectReward selectReward = new SelectReward();

        assertFalse("Should not be anyone's turn", selectReward.isPlayerTurn(1));
    }

    @Test
    public void testGetAvailableRewards() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red, Resource.Yellow);

        selectReward.setReward(1, card, rewards);

        List<Resource> available = selectReward.getAvailableRewards();
        assertEquals("Should have 3 available", 3, available.size());
        assertTrue("Should contain Green", available.contains(Resource.Green));
        assertTrue("Should contain Red", available.contains(Resource.Red));
        assertTrue("Should contain Yellow", available.contains(Resource.Yellow));
    }

    @Test
    public void testGetSelectedRewards() {
        SelectReward selectReward = new SelectReward();
        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);

        selectReward.setReward(1, card, rewards);

        List<Resource> selected = selectReward.getSelectedRewards();
        assertEquals("Should have 0 selected initially", 0, selected.size());

        // After selection (once Card is implemented):
        // selectReward.selectReward(Resource.Green);
        // selected = selectReward.getSelectedRewards();
        // assertEquals("Should have 1 selected", 1, selected.size());
        // assertTrue("Should contain Green", selected.contains(Resource.Green));
    }

    @Test
    public void testState() {
        SelectReward selectReward = new SelectReward();

        String state = selectReward.state();
        assertNotNull("State should not be null", state);
        assertTrue("State should indicate no selection",
                state.contains("no active selection"));

        Card card = createMockCard();
        List<Resource> rewards = List.of(Resource.Green, Resource.Red);
        selectReward.setReward(1, card, rewards);

        state = selectReward.state();
        assertTrue("State should contain player", state.contains("player: 1"));
        assertTrue("State should contain available", state.contains("available"));
    }

    @Test
    public void testMultipleSetReward() {
        SelectReward selectReward = new SelectReward();
        Card card1 = createMockCard();
        Card card2 = createMockCard();

        selectReward.setReward(1, card1, List.of(Resource.Green));
        assertEquals("Should be player 1", 1, (int) selectReward.getPlayer().get());

        selectReward.setReward(2, card2, List.of(Resource.Red, Resource.Yellow));
        assertEquals("Should be player 2", 2, (int) selectReward.getPlayer().get());
        assertEquals("Should have 2 rewards",
                2, selectReward.getAvailableRewards().size());
    }*/
}