package com.sunzy.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sunzy.reggie.common.BaseContext;
import com.sunzy.reggie.common.R;
import com.sunzy.reggie.domain.AddressBook;
import com.sunzy.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {

        // 1519141444042719234
        Long currentId = BaseContext.getCurrentId();
        log.info("登录用户id:{}", currentId);
        addressBook.setUserId(currentId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> defautAddress(@RequestBody AddressBook addressBook) {

        LambdaUpdateWrapper<AddressBook> qw = new LambdaUpdateWrapper<>();
        qw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        qw.set(AddressBook::getIsDefault, 0);

        addressBookService.update(qw);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefautAddress() {

        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(qw);

        if(addressBook == null){
            return R.error("没有默认地址！");
        }else{
            return R.success(addressBook);
        }
    }


    /**
     * 设置默认地址
     * @return
     */
    @DeleteMapping ("")
    public R<AddressBook> deleteAddress() {
        return null;
    }




    /**
     * 地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> lsit(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(addressBook.getUserId()!=null, AddressBook::getUserId, addressBook.getUserId());
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(addressBookLambdaQueryWrapper);

        return R.success(addressBookList);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

}
