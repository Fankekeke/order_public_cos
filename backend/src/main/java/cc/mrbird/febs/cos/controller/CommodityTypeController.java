package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.CommodityType;
import cc.mrbird.febs.cos.service.ICommodityTypeService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/room-type")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommodityTypeController {

    private final ICommodityTypeService roomTypeService;

    /**
     * 分页获取房间类型信息
     *
     * @param page          分页对象
     * @param commodityType 房间类型信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<CommodityType> page, CommodityType commodityType) {
        return R.ok(roomTypeService.selectRoomTypePage(page, commodityType));
    }

    /**
     * 获取房间类型信息
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(roomTypeService.list(Wrappers.<CommodityType>lambdaQuery().eq(CommodityType::getDelFlag, 0)));
    }

    /**
     * 获取房间类型详细信息
     *
     * @param id ID
     * @return 结果
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(roomTypeService.getById(id));
    }

    /**
     * 新增房间类型信息
     *
     * @param commodityType 房间类型信息
     * @return 结果
     */
    @PostMapping
    public R save(CommodityType commodityType) {
        commodityType.setCreateDate(DateUtil.formatDateTime(new Date()));
        commodityType.setCode("ROT-" + System.currentTimeMillis());
        return R.ok(roomTypeService.save(commodityType));
    }

    /**
     * 修改房间类型信息
     *
     * @param commodityType 房间类型信息
     * @return 结果
     */
    @PutMapping
    public R edit(CommodityType commodityType) {
        return R.ok(roomTypeService.updateById(commodityType));
    }

    /**
     * 删除房间类型信息
     *
     * @param ids 主键IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(roomTypeService.removeByIds(ids));
    }
}
