package shuhuai.wheremoney.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import shuhuai.wheremoney.utils.RequestGetter;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/manage")
@Tag(name = "管理管理")
@Slf4j
public class ManageController extends BaseController {
    @RequestMapping(value = "/log", method = RequestMethod.GET)
    @ResponseBody
    public void Download(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline;filename=" + java.net.URLEncoder.encode("log.log", StandardCharsets.UTF_8));
        byte[] buff = new byte[1024];
        BufferedInputStream bufferedInputStream = null;
        try {
            OutputStream outputStream = response.getOutputStream();
            String path = System.getProperty("user.dir");
            bufferedInputStream = new BufferedInputStream(new FileInputStream(path + "/logs/log.log"));
            int i = bufferedInputStream.read(buff);
            while (i != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                i = bufferedInputStream.read(buff);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            log.error(RequestGetter.getRequestUrl() + "：系统找不到指定的文件。");
        } catch (IOException e) {
            log.error(RequestGetter.getRequestUrl() + "：获取日志文件失败。");
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    log.error(RequestGetter.getRequestUrl() + "：获取日志文件失败。");
                }
            }
        }
        log.info(RequestGetter.getRequestUrl() + "：获取日志文件成功。");
    }
}