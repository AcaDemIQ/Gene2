package ru.nsu.kib.mukhin;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    private static InputStream is;
    private static int SIZE = 1500;
    private static int CHROMOSOME = 5;
    public static void main( String[] args ) throws IOException, CompoundNotFoundException, InterruptedException {
        is = App.class.getResourceAsStream("NC_003076.gbk");
        if (args.length < 1) throw new IllegalArgumentException();
        //is = new FileInputStream(args[0]);
        if (is == null){
            System.out.println("WTF");
            return;
        }
//        String[] cmd = new String[5];
//        cmd[0] = "grep";
//        cmd[1] = "-A";
//        cmd[2] = "1";
//        cmd[3] = "  gene  ";
//        cmd[4] = "/home/mukhin/Загрузки/CN2015EX-03/Gene2/src/resource/ru/nsu/kib/mukhin/App/NC_003070.gbk";
//        Process process = new ProcessBuilder(cmd).start();
//        InputStream is = process.getInputStream();
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
//
//        String line;
//        System.out.println("+++++++++++++++");
//        while ((line = br.readLine()) != null) {
//            if (line.equals("--")){
//                System.out.println("\n+++++++++++++++");
//                continue;
//            }
//
//            System.out.println(line);

//            String[] r = line.split("\\s+");
//            System.out.println(r[0] + r[2]);
//            System.out.println(r[0] + " " + r[1]);
//            String[] s = r[1].split("");
//            System.out.println(s[0]);
//            System.out.println("");
        //}

        Scanner scanner = new Scanner(is);
        List<Promotor> list = new ArrayList<>();
        System.out.println(scanner.hasNextLine());
        boolean scanningGeneAnnotation = true;
        StringBuilder builder = new StringBuilder();
        String l = null;
        while(scanner.hasNextLine()){

            l = scanner.nextLine();
            if (scanningGeneAnnotation && l.contains("ORIGIN      ")){//МЫ ДОШЛИ ДО ИСХОДНИОВ ХРОМОСОМЫ
                System.out.printf("Scanning annotations have done, size of list=%d\n", list.size());
                scanningGeneAnnotation = false;
//                System.out.println(l);
                continue;
            }

            if (scanningGeneAnnotation){
                if (l.contains("  gene  ")){
                    //System.out.println(l);
                    boolean b = l.contains("complement");
                    String[] q = l.split("(\\.\\.)|(\\s+complement\\()|(\\))|(\\s+<)|(\\s+)");
                    //System.out.println(q.length);
//                    for (String str : q){
//                        System.out.printf("|%s| ", str);
//                    }
                    l = scanner.nextLine();
                    String name = null;
                    if (l.contains("/gene=")){
                        String[] qq = l.split("(\\s+/gene=\")|\"");
//                    for (String str: qq){
//                        System.out.printf("{%s} ", str);
//                    }
                        if (qq.length == 2) name = qq[1];
                    }
                    //System.out.println(b);
                /*
                А ТЕПЕРЬ -- ВНИМАНИЕ
                q[1] -- просто "gene"
                q[2] -- начало
                q[3] -- конец
                b -- isComplement
                qq[1] --имя гена, если есть!
                 */
                    list.add(new Promotor(Integer.parseInt(q[2]), Integer.parseInt(q[3]), b, name));


                }
            } else {
                if (l.equals("//")) continue;
                String[] q = l.split("(\\s+\\d+)|\\s");
                for (String str: q){
                    //if (str.equals("")) continue;
                    builder.append(str);
                }
            }
        }
        System.out.printf("Program have build DNAString, size=%d", builder.length());
        //LinkedHashMap<String, DNASequence> dnaSequences = FastaReaderHelper.readFastaDNASequence(is);
        //System.out.println(dnaSequences);
//        GenbankReader<DNASequence, NucleotideCompound> dnaReader = new GenbankReader<DNASequence, NucleotideCompound>(
//                is,
//                new GenericGenbankHeaderParser<DNASequence,NucleotideCompound>(),
//                new DNASequenceCreator(DNACompoundSet.getDNACompoundSet())
//        );
//        dnaSequences = dnaReader.process();
//        is.close();
//        System.out.println(dnaSequences);
        for (Promotor p : list){
            if (!p.isComplement()) p.setRegion(builder.substring(p.getBegin() - SIZE  - 1< 0
                    ? 0
                    : p.getBegin() - SIZE  - 1,
                    p.getBegin() - 1));
            else p.setRegion(builder.substring(p.getEnd() + 1,
                    p.getEnd() + SIZE + 1> builder.length()
                    ? builder.length()
                    : p.getEnd() + SIZE + 1 ));
        }
        for (int i = 0; i < list.size(); i++){
            DNASequence dnaSequence = new DNASequence(list.get(i).getSequence().getSequenceAsString(), AmbiguityDNACompoundSet.getDNACompoundSet());
            //dnaSequence.setDescription("Some gene promoter named " + list.get(0).getName());
            StringBuilder stringBuilder = new StringBuilder("gi|name:");
            if (list.get(i).getName() == null) stringBuilder.append(" none|");
            else stringBuilder.append(list.get(i).getName() + "|");
            stringBuilder.append("complement:" + list.get(i).isComplement() + "|position:");
            if (!list.get(i).isComplement()) stringBuilder.append(list.get(i).getBegin() + "|");
            else stringBuilder.append(list.get(i).getEnd() + "|");

            stringBuilder.append("size:" + App.SIZE);
            stringBuilder.append("|chromosome:" + App.CHROMOSOME);
            dnaSequence.setAccession(new AccessionID(stringBuilder.toString()));
            try {
                FastaWriterHelper.writeSequence(new File("PROMOTOR_" + App.CHROMOSOME + "_" + i + ".fna"), dnaSequence);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    public App(){
        is = this.getClass().getResourceAsStream("NC_003070.gbk");
    }
}
