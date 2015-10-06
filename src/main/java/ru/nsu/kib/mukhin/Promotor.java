package ru.nsu.kib.mukhin;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.Sequence;

import javax.swing.text.View;

/**
 * Created by mukhin on 05.10.15.
 */
public class Promotor {
    private int begin;
    private int end;
    private boolean complement;
    private String name;
    private String region;

    private Sequence<NucleotideCompound> sequence;
    public Promotor(int _begin, int _end, boolean _b, String _str){
        begin = _begin;
        end = _end;
        complement = _b;
        name = _str;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public boolean isComplement() {
        return complement;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        AmbiguityDNACompoundSet ambiguityDNACompoundSet = AmbiguityDNACompoundSet.getDNACompoundSet();
        try {
            DNASequence s  = new DNASequence(region ,ambiguityDNACompoundSet);
            if (isComplement()) sequence = s.getComplement().getViewedSequence();//Просто комплиментарная
            else sequence = s;
            System.out.println("OK");
        } catch (CompoundNotFoundException e) {
            e.printStackTrace();
        }
//        if (!this.isComplement()) this.region = region;
//        else{
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < region.length(); i++){
//                switch (region.charAt(i)){
//                    case 'a':
//                        builder.append('t');
//                        break;
//                    case 'c':
//                        builder.append('g');
//                        break;
//                    case 'g':
//                        builder.append('c');
//                        break;
//                    case 't':
//                        builder.append('a');
//                        break;
//                    default:
//                        builder.append(region.charAt(i));
//                        System.err.println("Unregistered symbol: " + region.charAt(i));
//                }
//            }
//            this.region = builder.toString();
//        }
    }

    public Sequence<NucleotideCompound> getSequence() {
        return sequence;
    }
}
