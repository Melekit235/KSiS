import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }


    public static void checkHosts(String subnet) throws IOException {
        int timeout=100;
        long occurrencesCount = subnet.chars().filter(ch -> ch == '.').count();
        for (int i=1;i<256;i++){
            String host = subnet + i;
            if (occurrencesCount < 3) {
                host += '.';
                checkHosts(host);
            }
            else{
                System.out.print(host);
                if (InetAddress.getByName(host).isReachable(timeout)){
                    System.out.print (" is reachable ");
                }
                System.out.println("       "+checkARPTable(host));
            }
        }
    }


    private static String checkARPTable(String ip) throws IOException {
        String systemInput = takeARP(ip);
        String mac = "";
        Pattern pattern = Pattern.compile("\\s*([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
        Matcher matcher = pattern.matcher(systemInput);
        if (matcher.find()) {
            mac = mac + matcher.group().replaceAll("\\s", "");
        } else {
            mac = " No found ";
        }
        return mac;
    }

    private static String takeARP(String ip) throws IOException {
        String systemInput;
        Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a " + ip).getInputStream()).useDelimiter("\\A");
        systemInput = s.next();
        return systemInput;
    }


    public static void main(String[] args) throws IOException {

        Enumeration<NetworkInterface> networkInterfaces = null;
        networkInterfaces = NetworkInterface.getNetworkInterfaces();


        ArrayList<NetworkInterface> aNI = new ArrayList<>();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            byte[] hardwareAddress = new byte[0];
            hardwareAddress = ni.getHardwareAddress();

            if (hardwareAddress != null) {

                String[] hexadecimalFormat = new String[hardwareAddress.length];
                for (int i = 0; i < hardwareAddress.length; i++) {
                    hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                }

                System.out.print(String.join("-", hexadecimalFormat));
                System.out.print("   "+ni.getDisplayName()+"   ");
                Enumeration<InetAddress> i = ni.getInetAddresses();
                System.out.println(i.nextElement().getHostAddress());


                aNI.add(ni);
                if (!validate(aNI.get(aNI.size() - 1).getInetAddresses().nextElement().getHostAddress())) {
                    aNI.remove(aNI.size() - 1);
                }

            }
        }
        System.out.println();

        for (int i = 0; i < aNI.size(); i++) {

            byte[] hardwareAddress = new byte[0];
            hardwareAddress = aNI.get(i).getHardwareAddress();

            String[] hexadecimalFormat = new String[hardwareAddress.length];
            for (int j = 0; j < hardwareAddress.length; j++) {
                hexadecimalFormat[j] = String.format("%02X", hardwareAddress[j]);
            }

            System.out.print(i+"   ");
            System.out.print(String.join("-", hexadecimalFormat));
            System.out.print("   "+aNI.get(i).getDisplayName()+"   ");
            Enumeration<InetAddress> e = aNI.get(i).getInetAddresses();
            System.out.println(e.nextElement().getHostAddress());

        }
        System.out.println("Какую подсеть проверить");
        int s;
        Scanner in = new Scanner(System.in);
        s = in.nextInt();
        int z = aNI.get(s).getInterfaceAddresses().get(0).getNetworkPrefixLength();
        System.out.print("Префикс  ");
        System.out.print(z);

        String Net =  aNI.get(s).getInetAddresses().nextElement().getHostAddress();
        String subNet = new String();
        int count = 0;
        int i=0;
        while(count != z / 8){
            subNet += Net.charAt(i);
            if (Net.charAt(i) == '.')
                count++;

            i++;
        }
        System.out.print("  Подсеть  ");
        System.out.println(subNet);
        System.out.println();
        System.out.println();
        checkHosts(subNet);
    }
;}
