package ru.nsu.kib.mukhin;

import com.sun.deploy.util.StringUtils;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
//        is = App.class.getResourceAsStream("NC_003070.gbk");
        if (args.length < 1) throw new IllegalArgumentException();
        is = new FileInputStream(args[0]);
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
                continue;

            }

            if (scanningGeneAnnotation){
                if (l.contains("  gene  ")){
                    //System.out.println(l);
                    boolean b = l.contains("complement");
                    String[] q = l.split("(\\.\\.)|(\\s+complement\\()|(\\))|(\\s+<)|(\\s+)");
                    l = scanner.nextLine();
                    String name = null;
                    String locus = null;
                    ArrayList<String> gene_synonym = new ArrayList<>();
                    if (l.contains("/gene=")){
                        String[] qq = l.split("(\\s+/gene=\")|\"");
                        if (qq.length == 2) name = qq[1];
                        l = scanner.nextLine();
                    } if (l.contains("/locus_tag=")){
                        String[] qq = l.split("(\\s+/locus_tag=\")|\"");
                        locus = qq[1];
                        l = scanner.nextLine();
                    } if (l.contains("/gene_synonym=")){
                        /**
                        Здесь мы попытались получить всю запись о gene_synonym то есть сама строка и все то, что находится в кавычках
                         Они могут располагаться на нескольких строчках
                         */
                        StringBuilder bigString = new StringBuilder();
                        int count = 0;
                        while (count != 2){
                            int lastIndex = 0;
                            while (lastIndex != -1){
                                lastIndex = l.indexOf('\"', lastIndex);
                                if (lastIndex != -1){
                                    count++;
                                    lastIndex += 1;
                                }
                            }
                            bigString.append(l);
                            if (count != 2) l = scanner.nextLine();
                        }
                        /**
                         * Делим теперь сами синонимы(вводим новые "точки терминирования" на строке gene_synonym, кавычке и ; с пробелами
                         * Не забываем
                         */
                        Scanner s = new Scanner(bigString.toString()).useDelimiter(Pattern.compile("(\\s+/gene_synonym=\")|\"|(;\\s+)"));
//                        System.out.println(bigString.toString());
                        //s.next(Pattern.compile("(\\s+/gene_synonym=\")"));
                        while (s.hasNext()){
                            /**
                             * Хочу получить название синонима в нормальном виде, чтобы пробелов не было в начале и чтобы только один пробел был между словами
                             */
                            Scanner s1 = new Scanner(s.next());
                            StringBuilder b2 = new StringBuilder();
                            while (s1.hasNext()){
                                if (b2.length() != 0) b2.append(' ' + s1.next());
                                else b2.append(s1.next());
                            }
//                            System.out.println(b2.toString());
                            gene_synonym.add(b2.toString());
                        }


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
                    list.add(new Promotor(Integer.parseInt(q[2]), Integer.parseInt(q[3]), b, name, locus, gene_synonym));


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
            else p.setRegion(builder.substring(p.getEnd(),
                    p.getEnd() + SIZE > builder.length()
                    ? builder.length()
                    : p.getEnd() + SIZE));
        }
        for (int i = 0; i < list.size(); i++){
            DNASequence dnaSequence = new DNASequence(list.get(i).getSequence().getSequenceAsString(), AmbiguityDNACompoundSet.getDNACompoundSet());
            //dnaSequence.setDescription("Some gene promoter named " + list.get(0).getName());
            StringBuilder stringBuilder = new StringBuilder("gi|name:");
            if (list.get(i).getName() == null) stringBuilder.append("none|");
            else stringBuilder.append(list.get(i).getName() + "|");
            stringBuilder.append("locus:" + list.get(i).getLocus() + "|");

            stringBuilder.append("complement:" + list.get(i).isComplement() + "|begin:");
            if (!list.get(i).isComplement()) stringBuilder.append(list.get(i).getBegin() + "|end:" + list.get(i).getEnd());
            else stringBuilder.append(list.get(i).getEnd() + "|end:" + list.get(i).getBegin());

//            stringBuilder.append("size:" + App.SIZE);
            stringBuilder.append("|chromosome:" + App.CHROMOSOME);
            stringBuilder.append("|gene_synonym:" + list.get(i).getGene_synonym());
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
