package com.li.manager.common.controllor;

import com.li.manager.common.entity.Channel;
import com.li.manager.common.model.ChannelVo;
import com.li.manager.common.service.ChannelReactiveService;
import com.li.manager.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author li-yuanwen
 * 渠道商相关api
 */
@RestController
@Slf4j
@RequestMapping("/channel")
@Api(tags = "渠道相关接口")
public class ChannelController {

    @Autowired
    private ChannelReactiveService channelReactiveService;


    @ApiOperation("权限内所有渠道信息")
    @GetMapping(value = "/info")
    @PreAuthorize("hasAnyRole('ADMIN','CHANNEL','CHANNEL_INFO')")
    public Mono<Object> info() {
        return SecurityUtils.getCurrentUsername().flatMap(username-> channelReactiveService.info(username).collectList());
    }

    @ApiOperation("添加渠道")
    @PutMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','CHANNEL','CHANNEL_ADD')")
    public Mono<Object> addChannel(@RequestBody ChannelVo vo) {

        Channel channel = new Channel(vo.getId(), vo.getName(), vo.getLoginKey(), vo.getChargeKey(), vo.getWhiteIps(), vo.getWhiteAccounts());
        channelReactiveService.addChannel(channel);
        return Mono.just(new ResponseEntity<>(channel, HttpStatus.CREATED));
    }

    @ApiOperation("修改渠道")
    @PutMapping(value = "/modify")
    @PreAuthorize("hasAnyRole('ADMIN','CHANNEL','CHANNEL_MODIFY')")
    public Mono<Object> modifyOperator(@RequestBody ChannelVo vo) {

        Channel channel = new Channel(vo.getId(), vo.getName(), vo.getLoginKey(), vo.getChargeKey(), vo.getWhiteIps(), vo.getWhiteAccounts());
        channelReactiveService.modifyChannel(channel);
        return Mono.just(new ResponseEntity<>(channel, HttpStatus.OK));
    }


    @ApiOperation("删除渠道")
    @DeleteMapping(value = "/delete")
    @PreAuthorize("hasAnyRole('ADMIN','CHANNEL','CHANNEL_DELETE')")
    public Mono<Object> delOperator(@RequestBody int channelId) {
        channelReactiveService.delChannel(channelId);
        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }

}
