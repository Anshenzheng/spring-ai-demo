package org.an.springai.tools;

import org.springframework.ai.tool.annotation.Tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogTools {
    @Tool(description = "根据job name读取job 最新日志信息。入参是job name，出参是查询出的日志信息")
    String getLogContent(String jobName) throws IOException {
        String filePath = "E:\\Annan\\practise\\logs\\";
        return getLatestTwoFileContents(filePath, jobName);
    }

    /**
     * 查找目录中文件名包含关键字的最新两个文件，并返回其内容
     * @param filePath 要搜索的目录路径
     * @param keyword 文件名中需要包含的关键字
     * @return 包含两个文件内容的列表，按文件更新时间降序排列（最新的在前）
     * @throws IOException 当目录不存在或读取文件失败时抛出
     */
    public static String getLatestTwoFileContents(String filePath, String keyword) throws IOException {
        // 验证输入参数
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("目录路径不能为空");
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("关键字不能为空");
        }

        Path directory = Paths.get(filePath);

        // 检查路径是否存在且是目录
        if (!Files.exists(directory)) {
            throw new FileNotFoundException("目录不存在: " + filePath);
        }
        if (!Files.isDirectory(directory)) {
            throw new IOException("指定路径不是目录: " + filePath);
        }

        // 收集所有文件名包含关键字的文件及其最后修改时间
        List<FileInfo> matchingFiles = new ArrayList<>();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                // 检查文件名是否包含关键字（不区分大小写）
                if (fileName.toLowerCase().contains(keyword.toLowerCase())) {
                    matchingFiles.add(new FileInfo(file, attrs.lastModifiedTime().toMillis()));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 忽略访问失败的文件
                return FileVisitResult.CONTINUE;
            }
        });

        // 按最后修改时间降序排序（最新的在前）
        List<FileInfo> sortedFiles = matchingFiles.stream()
                .sorted((f1, f2) -> Long.compare(f2.lastModified, f1.lastModified))
                .collect(Collectors.toList());

        // 取前两个文件并读取内容
        String fileContent = "";
        if(sortedFiles !=null && !sortedFiles.isEmpty()){
            Path file = sortedFiles.get(0).path;
            byte[] contentBytes = Files.readAllBytes(file);
            fileContent = fileContent.concat(new String(contentBytes, StandardCharsets.UTF_8));
        }else{
            throw new IOException("没有找到文件名包含"+keyword+"的相关日志文件" );
        }


        return fileContent;
    }

    // 辅助类：存储文件路径和最后修改时间
    private static class FileInfo {
        Path path;
        long lastModified;

        FileInfo(Path path, long lastModified) {
            this.path = path;
            this.lastModified = lastModified;
        }
    }


}
