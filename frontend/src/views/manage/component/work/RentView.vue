<template>
  <a-modal v-model="show" title="预订" @cancel="onClose" :width="900">
    <template slot="footer">
      <a-button key="back1" @click="addOrder">
        预订
      </a-button>
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="rentData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">预订信息</span></a-col>
        <a-col :span="8"><b>开始时间：</b>
          {{ startDate }}
        </a-col>
        <a-col :span="8"><b>结束时间：</b>
          {{ endDate }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">信息</span></a-col>
        <a-col :span="8"><b>名称：</b>
          {{ rentData.name !== null ? rentData.name : '- -' }}
        </a-col>
        <a-col :span="8"><b>楼层：</b>
          {{ rentData.floor !== null ? rentData.floor : '- -' }}
        </a-col>
        <a-col :span="8"><b>价格/日：</b>
          {{ rentData.rentPrice !== null ? rentData.rentPrice : '- -' }}元
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>发布时间：</b>
          {{ rentData.createDate !== null ? rentData.createDate : '- -' }}
        </a-col>
        <a-col :span="8"><b>负责人：</b>
          {{ rentData.contact !== null ? rentData.contact : '- -' }}
        </a-col>
        <a-col :span="8"><b>联系方式：</b>
          {{ rentData.phone !== null ? rentData.phone : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>大小：</b>
          {{ rentData.roomSize !== null ? rentData.roomSize : '- -' }}
        </a-col>
        <a-col :span="8"><b>编号：</b>
          {{ rentData.code !== null ? rentData.code : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row :gutter="10" style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">包含设施</span></a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasBed == 1 ? 'green' : 'red'}">床</div>
          <img src="http://127.0.0.1:9527/imagesWeb/chuang.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasRefrigerator == 1 ? 'green' : 'red'}">冰箱</div>
          <img src="http://127.0.0.1:9527/imagesWeb/dankaimenbingxiang.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasAirConditioner == 1 ? 'green' : 'red'}">空调</div>
          <img src="http://127.0.0.1:9527/imagesWeb/kongtiao.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasBroadband == 1 ? 'green' : 'red'}">宽带</div>
          <img src="http://127.0.0.1:9527/imagesWeb/kuandai.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasBalcony == 1 ? 'green' : 'red'}">阳台</div>
          <img src="http://127.0.0.1:9527/imagesWeb/loutianyangtai.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasGasStoves == 1 ? 'green' : 'red'}">燃气灶</div>
          <img src="http://127.0.0.1:9527/imagesWeb/ranqizao.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasWaterHeater == 1 ? 'green' : 'red'}">热水器</div>
          <img src="http://127.0.0.1:9527/imagesWeb/reshuiqi.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasSofa == 1 ? 'green' : 'red'}">沙发</div>
          <img src="http://127.0.0.1:9527/imagesWeb/shafa_1.png">
        </a-col>
        <a-col :span="24" style="margin: 20px 0px"></a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasCook == 1 ? 'green' : 'red'}">做饭</div>
          <img src="http://127.0.0.1:9527/imagesWeb/weibolu.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasBathroom == 1 ? 'green' : 'red'}">卫生间</div>
          <img src="http://127.0.0.1:9527/imagesWeb/weiyu.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasTelevision == 1 ? 'green' : 'red'}">电视</div>
          <img src="http://127.0.0.1:9527/imagesWeb/dianshi.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasWashingMachine == 1 ? 'green' : 'red'}">洗衣机</div>
          <img src="http://127.0.0.1:9527/imagesWeb/xiyiji.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasWardrobe == 1 ? 'green' : 'red'}">衣柜</div>
          <img src="http://127.0.0.1:9527/imagesWeb/yigui.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasRangeHood == 1 ? 'green' : 'red'}">油烟机</div>
          <img src="http://127.0.0.1:9527/imagesWeb/youyanji.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasHeating == 1 ? 'green' : 'red'}">暖气</div>
          <img src="http://127.0.0.1:9527/imagesWeb/zaotai.png">
        </a-col>
        <a-col :span="3" style="text-align: center">
          <div :style="{color: rentData.hasDoorLock == 1 ? 'green' : 'red'}">智能门锁</div>
          <img src="http://127.0.0.1:9527/imagesWeb/zhinengmensuo.png">
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">图片</span></a-col>
        <span v-if="fileList.length === 0">暂无图片</span>
        <a-upload
          name="avatar"
          action="http://127.0.0.1:9527/file/fileUpload/"
          list-type="picture-card"
          :file-list="fileList"
          @preview="handlePreview"
          @change="picHandleChange"
        >
        </a-upload>
        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
          <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">备注</span></a-col>
        {{ rentData.content !== null ? rentData.content : '- -' }}
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="typeInfo != null">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">类型</span></a-col>
        <a-col :span="8"><b>类型编号：</b>
          {{ typeInfo.code !== null ? typeInfo.code : '- -' }}
        </a-col>
        <a-col :span="8"><b>类型：</b>
          {{ typeInfo.typeName !== null ? typeInfo.typeName : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="typeInfo != null">
        <a-col :span="24"><b>备注：</b>
          {{ typeInfo.remark !== null ? typeInfo.remark : '- -' }}
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'
import {mapState} from 'vuex'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
moment.locale('zh-cn')
export default {
  name: 'rentView',
  props: {
    rentShow: {
      type: Boolean,
      default: false
    },
    rentData: {
      type: Object
    },
    startDate: {
      type: String
    },
    endDate: {
      type: String
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    show: {
      get: function () {
        return this.rentShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      community: null,
      houseInfo: null,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      roomInfo: null,
      typeInfo: null
    }
  },
  watch: {
    rentShow: function (value) {
      if (value) {
        this.fileList = []
        this.selectDetail(this.rentData.id)
        this.imagesInit(this.rentData.images)
      }
    }
  },
  methods: {
    addOrder () {
      let data = { roomCode: this.rentData.code, startDate: this.startDate, endDate: this.endDate, rentDay: this.rentData.rentPrice, userId: this.user.userId }
      this.$post('/cos/order-info', data).then((r) => {
        this.$emit('success')
      })
    },
    selectDetail (roomId) {
      this.$get(`/cos/room-info/detail/${roomId}`).then((r) => {
        this.roomInfo = r.data.room
        this.typeInfo = r.data.type
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    getCommunity (communityCode) {
      this.$get(`/cos/community-info/detail/${communityCode}`).then((r) => {
        this.community = r.data.data
      })
    },
    getHouseInfo (houseCode) {
      this.$get(`/cos/house-info/detail/${houseCode}`).then((r) => {
        this.houseInfo = r.data.data
        this.getCommunity(this.houseInfo.communityCode)
      })
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
/deep/ .ant-table-tbody {
  font-size: 12px;
}
/deep/ .ant-table-thead {
  font-size: 13px;
}
</style>
