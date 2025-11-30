package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.List;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

public class Card {
    private Resource[] resources;
    private int pollutionSpace;
    public Card(){}

    public boolean canGetResource(List<Resource> res){
        return true;
    }

    public List<Resource> getResources(List<Resource> res){
        return null;
    }

    public boolean canPutResource(List<Resource> res){
        return true;
    }

    public void putResource(List<Resource> res){}

    public boolean check(List<Resource> input, List<Resource> output, int polution){
        return true;
    }

    public boolean checkLower(List<Resource> input, List<Resource> output, int polution){
        return true;
    }

    public boolean hasAssistance(){
        return true;
    }

    public String state(){
        return "";
    }
}
