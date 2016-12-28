package jp.co.tv.excelmetaforce;

import org.apache.commons.lang3.StringUtils;

public final class RunnerHelper {
    private RunnerHelper() {}
    
    /**
     * get file name from first command line arguments.
     * if empty, force input file name from  console. 
     * 
     * @param args command line arguments
     * @return filename
     */
    public static String getFileName(String... args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in, "UTF-8");
        String excelFileName = args.length == 0 ? "" : args[0];
        
        while (StringUtils.isEmpty(excelFileName)) {
            System.out.print("need file name > ");
            excelFileName = scanner.nextLine();
        }
        scanner.close();
        
        return excelFileName;
    }
}
