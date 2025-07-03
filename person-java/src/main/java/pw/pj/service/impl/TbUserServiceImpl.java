package pw.pj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pw.pj.POJO.DO.TbUser;
import pw.pj.service.TbUserService;
import pw.pj.mapper.TbUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 北煜
* @description 针对表【tb_user(用户表)】的数据库操作Service实现
* @createDate 2025-07-01 16:44:02
*/
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser>
    implements TbUserService{

}




