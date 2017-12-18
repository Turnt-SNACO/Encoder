import java.util.Scanner;

public class Encoder {
    private char base_char[], code_char[];
    String map[];
    private int
            shift_amount,           //can be anything I guess
            grouping,               //how many chars per group
            sets;                   //how many sets of char groupings
    public Encoder(int pairing_type, int shift_amount){     //expect int value of 1-5 for pairing type

        base_char = new char[126];
        code_char = new char[126];
        map = new String[126];
        int random;
        for (int x=0;x<126;x++) {
            base_char[x] = (char) x;        //initialize arrays with default ascii characters 0-126 (no delete 127)
            code_char[x] = (char) x;
        }
        for (int x=0; x<126;x++){
            map[x] = "";
            for (int y=0;y<5;y++){             // generate random 5 character strings to map characters to
                random = (int)(Math.random()*94)+32;
                map[x]=map[x]+base_char[random];
            }
        }
        setPairing_type(pairing_type);
        setShift_amount(shift_amount);
    }
    private void alignArray(){
        char temp[][] = new char[sets][grouping];   //temporary double array for manipulation
        int y=0,z=0,x,round_robin=0;                                //y is the index for the first dimension of temp
        for (x=0;x<126;x++){                    //z is the index for the second dimension
            if (x%grouping==0&&x!=0){               //every time we hit a multiple of the grouping number
                y++;                                //move to the next set and set y index to
                z=0;
            }
            temp[y][z]=base_char[x];                //fingers crossed
            z++;
        }
        x=0;
        for(y=0;y<sets;y++){
            for (z=0;z<grouping;z++){
                if (z+shift_amount>grouping-1){                 //if the shift will exceed the array index
                    code_char[x++] = temp[y][round_robin++];    //go back to the beginning and increment roundrobin
                }else {
                    code_char[x++] = temp[y][z + shift_amount]; //else its a direct map
                }
            }
            round_robin=0;
        }
    }
    public int getShift_amount(){
        return shift_amount;
    }
    public int getGrouping(){
        return grouping;
    }
    public void setPairing_type(int pairing_type){
        switch(pairing_type){       //based on usable factors of 126
            case 1:                 //multiply grouping by sets and you'll get 126
                grouping=63;
                sets=2;
                break;
            case 2:
                grouping=42;
                sets=3;
                break;
            case 3:
                grouping=21;
                sets=6;
                break;
            case 4:
                grouping=18;
                sets=7;
                break;
            case 5:
                grouping=14;
                sets=9;
                break;
            default:
                grouping=18;
                sets=6;
                break;
        }
        alignArray();
    }
    public void setShift_amount(int shift_amount){
        this.shift_amount=shift_amount;
        alignArray();
    }
    public String coded(String message){            //takes message and remaps each character to the character
        char msg[] = message.toCharArray();         //at the same position in the code_char array
        String output = "";
        for (int x=0;x<message.length();x++){
            for (int y=0; y<126; y++){
                if (msg[x]==base_char[y])
                    output+=code_char[y];
            }
        }
        return output;
    }
    public String decoded(String message){      //does the exact same thing as coded but the other way around
        char msg[] = message.toCharArray();
        String output = "";
        for (int x=0;x<message.length();x++){
            for (int y=0; y<126; y++){
                if (msg[x]==code_char[y])
                    output+=base_char[y];
            }
        }
        return output;
    }
    public String codedWithString(String message){  //does the same thing as coded but maps it map[] array instead
        char msg[] = message.toCharArray();
        String output = "";
        for (int x=0; x<message.length();x++){
            for (int y=0; y<126;y++){
                if (msg[x]==base_char[y]){
                    output+=map[y];
                }
            }
        }
        return output;
    }
    public String decodedWithString(String message){       //does same thing as decoded but with the map[] array
        String output = "";
        for (int x=0; x<message.length();x+=5){
            for (int y=0; y<126;y++){
                if (message.substring(x,x+5).equals(map[y])){
                    output+=base_char[y];
                }
            }
        }
        return output;
    }
    public static void main(String [] args){            //console app code
        String input,holder="";
        Boolean command_executed=false;                 //bullshit hackjob way of preventing accidental default
        int in_i;
        String in_s;
        Encoder e = new Encoder(3,10);                  //default 310
        Scanner s = new Scanner(System.in);
        do{
            System.out.print("> ");
            input = s.nextLine();
            switch (input){
                default:
                    if (!command_executed) {
                        System.out.println("Invalid command.  Valid commands are:");
                        System.out.println("    [super] encode");
                        System.out.println("    [super] decode");
                        System.out.println("    change shift");
                        System.out.println("    change pairing type");
                        System.out.println("    exit");
                    }
                    command_executed=false;
                    break;
                case "change shift":
                    System.out.print("New shift(1-"+(e.getGrouping()-1)+"): ");
                    in_i = s.nextInt();
                    if (in_i<e.getGrouping()) {
                        e.setShift_amount(in_i);
                        System.out.println("Shift successfully set to " + in_i + ".");
                    }else
                        System.out.println("Invalid shift.");
                    command_executed=true;
                    break;
                case "change pairing type":
                    System.out.print("New pairing type(1-5): ");
                    in_i = s.nextInt();
                    if (in_i<=5&&in_i>=1) {
                        e.setPairing_type(in_i);
                        System.out.println("Pairing type successfully set to "+in_i+".");
                    }else
                        System.out.println("Invalid pairing type.");
                    command_executed=true;
                    break;
                case "encode":
                    System.out.print("Message to encode: ");
                    in_s=s.nextLine();
                    if (in_s.equals("")){
                        System.out.println("Message successfully encoded to: " + e.coded(holder));
                        holder=e.coded(holder);
                    }else {
                        System.out.println("Message successfully encoded to: " + e.coded(in_s));
                        holder = e.coded(in_s);
                    }
                    command_executed=true;
                    break;
                case "decode":
                    System.out.print("Message to decode: ");
                    in_s=s.nextLine();
                    if (in_s.equals("")){
                        System.out.println("Message successfully decoded to: "+e.decoded(holder));
                        holder = e.decoded(holder);
                    }else {
                        System.out.println("Message successfully decoded to: " + e.decoded(in_s));
                        holder = e.decoded(in_s);
                    }
                    command_executed=true;
                    break;
                case "super encode":
                    System.out.print("Message to super encode: ");
                    in_s=s.nextLine();
                    if (in_s.equals("")){
                        System.out.println("Message successfully encoded to: "+e.coded(e.codedWithString(holder)));
                        holder = e.coded(e.decodedWithString(holder));
                    }else {
                        System.out.println("Message successfully encoded to: " + e.coded(e.codedWithString(in_s)));
                        holder = e.coded(e.codedWithString(in_s));
                    }
                    command_executed=true;
                    break;
                case "super decode":
                    System.out.print("Message to decode: ");
                    in_s=s.nextLine();
                    if (in_s.equals("")){
                        System.out.println("Message successfully decoded to: "+e.decodedWithString(e.decoded(holder)));
                        holder = e.decodedWithString(e.decoded(holder));
                    }else {
                        System.out.println("Message successfully decoded to: " + e.decodedWithString(e.decoded(in_s)));
                        holder = e.decodedWithString(e.decoded(in_s));
                    }
                    command_executed=true;
                    break;
                case "help":
                    System.out.println("Valid commands are:");
                    System.out.println("    [super] encode");
                    System.out.println("    [super] decode");
                    System.out.println("    change shift");
                    System.out.println("    change pairing type");
                    System.out.println("    exit");
                    command_executed=true;
                    break;
            }
        }while(!input.equals("exit"));
    }
}
