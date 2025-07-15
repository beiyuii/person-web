package pw.pj.controller.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试第一优先级的基础组件功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试成功响应（无数据）
     * 
     * @return 成功响应
     */
    @GetMapping("/success")
    public ApiResponse<Void> testSuccess() {
        log.info("测试成功响应");
        return ApiResponse.success();
    }

    /**
     * 测试成功响应（带数据）
     * 
     * @return 成功响应
     */
    @GetMapping("/success-data")
    public ApiResponse<Map<String, Object>> testSuccessWithData() {
        log.info("测试成功响应（带数据）");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello World");
        data.put("timestamp", LocalDateTime.now());
        data.put("number", 12345);
        data.put("boolean", true);
        data.put("nullValue", null);

        return ApiResponse.success(data);
    }

    /**
     * 测试业务异常
     * 
     * @return 永远不会返回，会抛出异常
     */
    @GetMapping("/business-error")
    public ApiResponse<Void> testBusinessError() {
        log.info("测试业务异常");
        throw BusinessException.of(ResultEnum.USER_NOT_FOUND);
    }

    /**
     * 测试自定义业务异常
     * 
     * @return 永远不会返回，会抛出异常
     */
    @GetMapping("/custom-error")
    public ApiResponse<Void> testCustomError() {
        log.info("测试自定义业务异常");
        throw BusinessException.of(1999, "这是一个自定义的业务异常");
    }

    /**
     * 测试参数验证异常
     * 
     * @param dto 测试数据传输对象
     * @return 验证通过的响应
     */
    @PostMapping("/validate")
    public ApiResponse<TestDTO> testValidate(@Valid @RequestBody TestDTO dto) {
        log.info("测试参数验证，接收到数据: {}", dto);
        return ApiResponse.success("验证通过", dto);
    }

    /**
     * 测试分页查询
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<TestItem>> testPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("测试分页查询，页码: {}, 每页大小: {}", page, size);

        // 模拟查询数据
        List<TestItem> allData = createTestData(100); // 创建100条测试数据

        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        List<TestItem> pageData = new ArrayList<>();

        // 模拟分页逻辑（实际应用中这里应该是数据库查询）
        int start = (page - 1) * size;
        int end = Math.min(start + size, allData.size());

        if (start < allData.size()) {
            pageData = allData.subList(start, end);
        }

        // 手动创建PageInfo（因为这里是模拟数据）
        PageInfo<TestItem> pageInfo = new PageInfo<>(pageData);
        pageInfo.setTotal(allData.size());
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(size);
        pageInfo.setPages((int) Math.ceil((double) allData.size() / size));

        PageResult<TestItem> result = PageResult.of(pageInfo);

        return ApiResponse.success(result);
    }

    /**
     * 测试空指针异常
     * 
     * @return 永远不会返回，会抛出异常
     */
    @GetMapping("/null-pointer")
    public ApiResponse<Void> testNullPointer() {
        log.info("测试空指针异常");
        String str = null;
        str.length(); // 故意触发空指针异常
        return ApiResponse.success();
    }

    /**
     * 测试非法参数异常
     * 
     * @return 永远不会返回，会抛出异常
     */
    @GetMapping("/illegal-argument")
    public ApiResponse<Void> testIllegalArgument() {
        log.info("测试非法参数异常");
        throw new IllegalArgumentException("这是一个非法参数异常");
    }

    /**
     * 测试运行时异常
     * 
     * @return 永远不会返回，会抛出异常
     */
    @GetMapping("/runtime-error")
    public ApiResponse<Void> testRuntimeError() {
        log.info("测试运行时异常");
        throw new RuntimeException("这是一个运行时异常");
    }

    /**
     * 测试请求参数缺失异常
     * 
     * @param requiredParam 必需参数
     * @return 成功响应
     */
    @GetMapping("/missing-param")
    public ApiResponse<String> testMissingParam(@RequestParam String requiredParam) {
        log.info("测试请求参数，参数值: {}", requiredParam);
        return ApiResponse.success("参数接收成功: " + requiredParam);
    }

    /**
     * 测试数据转换
     * 
     * @return 转换后的分页结果
     */
    @GetMapping("/convert")
    public ApiResponse<PageResult<String>> testConvert() {
        log.info("测试数据转换");

        // 创建原始分页数据
        List<TestItem> items = createTestData(5);
        PageResult<TestItem> originalResult = PageResult.of(items, 5L, 1, 10);

        // 转换为字符串类型
        PageResult<String> convertedResult = originalResult
                .convert(item -> String.format("ID: %d, Name: %s", item.getId(), item.getName()));

        return ApiResponse.success(convertedResult);
    }

    /**
     * 创建测试数据
     * 
     * @param count 数据数量
     * @return 测试数据列表
     */
    private List<TestItem> createTestData(int count) {
        List<TestItem> data = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            TestItem item = new TestItem();
            item.setId((long) i);
            item.setName("测试项目 " + i);
            item.setDescription("这是第 " + i + " 个测试项目");
            item.setCreateTime(LocalDateTime.now().minusDays(i));
            data.add(item);
        }
        return data;
    }

    /**
     * 测试数据传输对象
     */
    @Data
    public static class TestDTO {
        @NotBlank(message = "姓名不能为空")
        private String name;

        @NotNull(message = "年龄不能为空")
        private Integer age;

        private String email;

        private LocalDateTime birthday;
    }

    /**
     * 测试项目对象
     */
    @Data
    public static class TestItem {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime createTime;
    }
}