package pw.pj;

import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础组件纯单元测试（不依赖Spring上下文）
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
class ComponentUnitTest {

    /**
     * 测试ApiResponse成功响应
     */
    @Test
    void testApiResponseSuccess() {
        System.out.println("=== 测试ApiResponse成功响应 ===");

        // 测试无数据成功响应
        ApiResponse<Void> response1 = ApiResponse.success();
        assertEquals(200, response1.getCode());
        assertEquals("操作成功", response1.getMessage());
        assertNull(response1.getData());
        assertTrue(response1.isSuccess());
        assertNotNull(response1.getTimestamp());
        System.out.println("✓ 无数据成功响应: " + response1);

        // 测试带数据成功响应
        String testData = "测试数据";
        ApiResponse<String> response2 = ApiResponse.success(testData);
        assertEquals(200, response2.getCode());
        assertEquals("操作成功", response2.getMessage());
        assertEquals(testData, response2.getData());
        assertTrue(response2.isSuccess());
        System.out.println("✓ 带数据成功响应: " + response2);

        // 测试自定义消息响应
        ApiResponse<String> response3 = ApiResponse.success("自定义成功消息", testData);
        assertEquals(200, response3.getCode());
        assertEquals("自定义成功消息", response3.getMessage());
        assertEquals(testData, response3.getData());
        System.out.println("✓ 自定义消息响应: " + response3);
        
        // 测试仅自定义消息响应（无数据）
        ApiResponse<Void> response4 = ApiResponse.successWithMessage("仅消息响应");
        assertEquals(200, response4.getCode());
        assertEquals("仅消息响应", response4.getMessage());
        assertNull(response4.getData());
        System.out.println("✓ 仅消息响应: " + response4);
    }

    /**
     * 测试ApiResponse错误响应
     */
    @Test
    void testApiResponseError() {
        System.out.println("\n=== 测试ApiResponse错误响应 ===");

        // 测试使用枚举的错误响应
        ApiResponse<Void> response1 = ApiResponse.error(ResultEnum.USER_NOT_FOUND);
        assertEquals(1007, response1.getCode());
        assertEquals("用户不存在", response1.getMessage());
        assertNull(response1.getData());
        assertFalse(response1.isSuccess());
        System.out.println("✓ 枚举错误响应: " + response1);

        // 测试自定义错误响应
        ApiResponse<Void> response2 = ApiResponse.error(9999, "自定义错误");
        assertEquals(9999, response2.getCode());
        assertEquals("自定义错误", response2.getMessage());
        assertFalse(response2.isSuccess());
        System.out.println("✓ 自定义错误响应: " + response2);

        // 测试验证错误响应
        List<String> errors = Arrays.asList("字段1错误", "字段2错误");
        ApiResponse<Void> response3 = ApiResponse.validateError(errors);
        assertEquals(400, response3.getCode());
        assertEquals("参数验证失败", response3.getMessage());
        assertEquals(errors, response3.getErrors());
        System.out.println("✓ 验证错误响应: " + response3);
    }

    /**
     * 测试PageResult分页结果
     */
    @Test
    void testPageResult() {
        System.out.println("\n=== 测试PageResult分页结果 ===");

        // 创建测试数据
        List<String> testData = Arrays.asList("项目1", "项目2", "项目3");

        // 测试基本分页结果创建
        PageResult<String> result1 = PageResult.of(testData, 23L, 2, 10);
        assertEquals(testData, result1.getRecords());
        assertEquals(23L, result1.getTotal());
        assertEquals(2, result1.getCurrent());
        assertEquals(10, result1.getSize());
        assertEquals(3, result1.getPages());
        assertTrue(result1.getHasPrevious());
        assertTrue(result1.getHasNext());
        assertFalse(result1.getIsFirst());
        assertFalse(result1.getIsLast());
        System.out.println("✓ 基本分页结果: " + result1);

        // 测试第一页
        PageResult<String> result2 = PageResult.of(testData, 23L, 1, 10);
        assertTrue(result2.getIsFirst());
        assertFalse(result2.getHasPrevious());
        System.out.println("✓ 第一页状态: 是第一页=" + result2.getIsFirst() + ", 有上一页=" + result2.getHasPrevious());

        // 测试最后一页
        PageResult<String> result3 = PageResult.of(testData, 23L, 3, 10);
        assertTrue(result3.getIsLast());
        assertFalse(result3.getHasNext());
        System.out.println("✓ 最后一页状态: 是最后一页=" + result3.getIsLast() + ", 有下一页=" + result3.getHasNext());

        // 测试空分页结果
        PageResult<String> emptyResult = PageResult.empty();
        assertTrue(emptyResult.getRecords().isEmpty());
        assertEquals(0L, emptyResult.getTotal());
        assertEquals(1, emptyResult.getCurrent());
        assertEquals(10, emptyResult.getSize());
        assertFalse(emptyResult.hasRecords());
        System.out.println("✓ 空分页结果: " + emptyResult);
    }

    /**
     * 测试PageResult数据转换
     */
    @Test
    void testPageResultConvert() {
        System.out.println("\n=== 测试PageResult数据转换 ===");

        // 创建原始数据
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        PageResult<Integer> originalResult = PageResult.of(numbers, 10L, 1, 5);
        System.out.println("原始数据: " + originalResult.getRecords());

        // 转换为字符串
        PageResult<String> convertedResult = originalResult.convert(num -> "数字: " + num);

        assertEquals(5, convertedResult.getRecords().size());
        assertEquals("数字: 1", convertedResult.getRecords().get(0));
        assertEquals("数字: 5", convertedResult.getRecords().get(4));
        assertEquals(10L, convertedResult.getTotal());
        assertEquals(1, convertedResult.getCurrent());
        assertEquals(5, convertedResult.getSize());
        System.out.println("✓ 转换后数据: " + convertedResult.getRecords());
    }

    /**
     * 测试PageResult的PageInfo创建
     */
    @Test
    void testPageResultFromPageInfo() {
        System.out.println("\n=== 测试PageResult的PageInfo创建 ===");

        // 创建PageInfo
        List<String> data = Arrays.asList("A", "B", "C");
        PageInfo<String> pageInfo = new PageInfo<>(data);
        pageInfo.setTotal(15);
        pageInfo.setPageNum(2);
        pageInfo.setPageSize(5);
        System.out.println("PageInfo数据: " + pageInfo.getList());

        // 从PageInfo创建PageResult
        PageResult<String> result = PageResult.of(pageInfo);
        assertEquals(data, result.getRecords());
        assertEquals(15, result.getTotal());
        assertEquals(2, result.getCurrent());
        assertEquals(5, result.getSize());
        System.out.println("✓ 从PageInfo创建的PageResult: " + result);
    }

    /**
     * 测试PageResult的辅助方法
     */
    @Test
    void testPageResultHelperMethods() {
        System.out.println("\n=== 测试PageResult的辅助方法 ===");

        List<String> data = Arrays.asList("A", "B", "C");
        PageResult<String> result = PageResult.of(data, 23L, 3, 10);

        // 测试hasRecords
        assertTrue(result.hasRecords());
        System.out.println("✓ hasRecords: " + result.hasRecords());

        // 测试getCurrentPageSize
        assertEquals(3, result.getCurrentPageSize());
        System.out.println("✓ getCurrentPageSize: " + result.getCurrentPageSize());

        // 测试getStartRow和getEndRow
        assertEquals(21, result.getStartRow());
        assertEquals(23, result.getEndRow());
        System.out.println("✓ 记录范围: " + result.getStartRow() + " - " + result.getEndRow());

        // 测试空结果
        PageResult<String> emptyResult = PageResult.empty();
        assertFalse(emptyResult.hasRecords());
        assertEquals(0, emptyResult.getCurrentPageSize());
        assertEquals(0, emptyResult.getStartRow());
        assertEquals(0, emptyResult.getEndRow());
        System.out.println("✓ 空结果的辅助方法: hasRecords=" + emptyResult.hasRecords() +
                ", size=" + emptyResult.getCurrentPageSize() +
                ", range=" + emptyResult.getStartRow() + "-" + emptyResult.getEndRow());
    }

    /**
     * 测试BusinessException业务异常
     */
    @Test
    void testBusinessException() {
        System.out.println("\n=== 测试BusinessException业务异常 ===");

        // 测试使用枚举创建异常
        BusinessException exception1 = BusinessException.of(ResultEnum.LOGIN_ERROR);
        assertEquals(1001, exception1.getCode());
        assertEquals("用户名或密码错误", exception1.getMessage());
        System.out.println("✓ 枚举异常: " + exception1);

        // 测试使用枚举和自定义消息创建异常
        BusinessException exception2 = BusinessException.of(ResultEnum.LOGIN_ERROR, "登录失败，请重试");
        assertEquals(1001, exception2.getCode());
        assertEquals("登录失败，请重试", exception2.getMessage());
        System.out.println("✓ 枚举+自定义消息异常: " + exception2);

        // 测试使用状态码和消息创建异常
        BusinessException exception3 = BusinessException.of(9999, "自定义异常");
        assertEquals(9999, exception3.getCode());
        assertEquals("自定义异常", exception3.getMessage());
        System.out.println("✓ 状态码+消息异常: " + exception3);

        // 测试仅使用消息创建异常
        BusinessException exception4 = BusinessException.of("简单异常");
        assertEquals(500, exception4.getCode());
        assertEquals("简单异常", exception4.getMessage());
        System.out.println("✓ 简单消息异常: " + exception4);
    }

    /**
     * 测试ResultEnum枚举
     */
    @Test
    void testResultEnum() {
        System.out.println("\n=== 测试ResultEnum枚举 ===");

        // 测试基本属性
        assertEquals(200, ResultEnum.SUCCESS.getCode());
        assertEquals("操作成功", ResultEnum.SUCCESS.getMessage());
        System.out.println("✓ SUCCESS枚举: " + ResultEnum.SUCCESS.getCode() + " - " + ResultEnum.SUCCESS.getMessage());

        assertEquals(1007, ResultEnum.USER_NOT_FOUND.getCode());
        assertEquals("用户不存在", ResultEnum.USER_NOT_FOUND.getMessage());
        System.out.println("✓ USER_NOT_FOUND枚举: " + ResultEnum.USER_NOT_FOUND.getCode() + " - "
                + ResultEnum.USER_NOT_FOUND.getMessage());

        // 测试根据代码查找枚举
        assertEquals(ResultEnum.SUCCESS, ResultEnum.getByCode(200));
        assertEquals(ResultEnum.USER_NOT_FOUND, ResultEnum.getByCode(1007));
        assertNull(ResultEnum.getByCode(99999));
        System.out.println("✓ 根据代码查找: 200=" + ResultEnum.getByCode(200) + ", 1007=" + ResultEnum.getByCode(1007)
                + ", 99999=" + ResultEnum.getByCode(99999));

        // 测试成功状态判断
        assertTrue(ResultEnum.isSuccess(200));
        assertFalse(ResultEnum.isSuccess(500));
        assertFalse(ResultEnum.isSuccess(1007));
        System.out.println("✓ 成功状态判断: 200=" + ResultEnum.isSuccess(200) + ", 500=" + ResultEnum.isSuccess(500)
                + ", 1007=" + ResultEnum.isSuccess(1007));
    }

    /**
     * 测试时间序列化
     */
    @Test
    void testTimeHandling() {
        System.out.println("\n=== 测试时间处理 ===");

        LocalDateTime now = LocalDateTime.now();
        ApiResponse<LocalDateTime> response = ApiResponse.success(now);

        // 验证时间戳格式
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        System.out.println("✓ 时间戳格式: " + response.getTimestamp());

        // 验证数据包含时间
        assertEquals(now, response.getData());
        System.out.println("✓ 时间数据: " + response.getData());
    }

    /**
     * 测试null值处理
     */
    @Test
    void testNullHandling() {
        System.out.println("\n=== 测试null值处理 ===");

        ApiResponse<String> response = ApiResponse.success(null);
        assertNull(response.getData());
        assertTrue(response.isSuccess());
        System.out.println("✓ null值响应: " + response);
        System.out.println("✓ 成功状态: " + response.isSuccess());
    }

    /**
     * 测试异常的toString方法
     */
    @Test
    void testExceptionToString() {
        System.out.println("\n=== 测试异常的toString方法 ===");

        BusinessException exception = BusinessException.of(ResultEnum.USER_NOT_FOUND);
        String toString = exception.toString();
        assertTrue(toString.contains("BusinessException"));
        assertTrue(toString.contains("1007"));
        assertTrue(toString.contains("用户不存在"));
        System.out.println("✓ 异常toString: " + toString);
    }

    /**
     * 综合测试：模拟真实使用场景
     */
    @Test
    void testRealWorldScenario() {
        System.out.println("\n=== 综合测试：模拟真实使用场景 ===");

        try {
            // 模拟查询用户列表
            List<String> users = Arrays.asList("张三", "李四", "王五");
            PageResult<String> pageResult = PageResult.of(users, 50L, 1, 10);

            ApiResponse<PageResult<String>> successResponse = ApiResponse.success(pageResult);
            System.out.println("✓ 成功查询用户列表: " + successResponse);

            // 模拟用户不存在的情况
            throw BusinessException.of(ResultEnum.USER_NOT_FOUND, "用户ID不存在");

        } catch (BusinessException e) {
            ApiResponse<Void> errorResponse = ApiResponse.error(e.getCode(), e.getMessage());
            System.out.println("✓ 业务异常处理: " + errorResponse);

            // 验证异常处理正确
            assertEquals(1007, e.getCode());
            assertEquals("用户ID不存在", e.getMessage());
        }

        System.out.println("✓ 综合测试完成");
    }
}