package com.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExampleApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleApplication.class);
    static String COM_PATH = "/pkg/";
    static String F_IDX = ".fileList.idx";
    static String LINE_SPLIT = "\n";

    public static void main(String[] args) {
        OnlyLocalOk(COM_PATH);
        ALLRunTimeOk(COM_PATH, "*.xml", "*.json");
    }

    public static void ALLRunTimeOk(String path, String... files) {
        for (String file : files) {
            String idxf = file.substring(file.indexOf(".") + 1) + F_IDX;
            List<String> fs = new ArrayList<>();
            Resource i = resolver.getResource(path + idxf);
            if (i != null && i.exists()) {
                String fileList = null;
                try {
                    fileList = StreamUtils.copyToString(i.getInputStream(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<String> fsi = Arrays.stream(fileList.trim().split(LINE_SPLIT)).collect(Collectors.toList());
                if (fsi.size() > 0) fs = fsi;
            }
            if (fs.size() < 1) {//local src generate iff
                fs = getRes(path + file);
                Resource fo = resolver.getResource(path);
                File iff = null;
                try {
                    String targetPath = fo.getURL().getPath();
                    targetPath = targetPath.substring(targetPath.indexOf("/"), targetPath.indexOf("target"));
                    File f = new File(targetPath + "/src");
                    if (f.isDirectory()) {
                        File parentPath = findSrcPath(f, path.endsWith("/") ? path.substring(0, path.length() - 1) : path).get(0);
                        iff = new File(parentPath.getPath() + "/" + idxf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (iff.exists()) {
                    iff.delete();
                    try {
                        iff.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                BufferedWriter out = null;
                try {
                    out = new BufferedWriter(new FileWriter(iff));
                    out.write((fs.stream().collect(Collectors.joining(LINE_SPLIT))).trim());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null)
                            out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            fs.forEach(f -> {
                try {
                    LOG.info("fs" + f + ",content:" + StreamUtils.copyToString(resolver.getResource(path + f).getInputStream(), Charset.defaultCharset()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private static List<File> findSrcPath(File f, String path) {
        List<File> ls = new ArrayList<>();
        File[] sc = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    ls.addAll(findSrcPath(file, path));
                }
                return file.isDirectory() && file.getPath().replaceAll("\\\\", "/").endsWith(path);
            }
        });
        if (sc != null && sc.length > 0) ls.addAll(Arrays.stream(sc).collect(Collectors.toList()));
        return ls;
    }

    public static void OnlyLocalOk(String path) {
        List<String> fs = getRes(path + "*");
        fs.forEach(f -> {
            try {
                LOG.info("fs" + f + ",content:" + StreamUtils.copyToString(resolver.getResource(path + f).getInputStream(), Charset.defaultCharset()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /* 获取resource下path目录的json文件资源列表 */
    public static List<String> getRes(String path) {
        List set = new ArrayList();
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Arrays.stream(resources).forEach(resource -> {
            try {
                set.add(resource.getFilename());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return set;
    }
}
