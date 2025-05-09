<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
          v-model="keywords"
          prefix-icon="el-icon-search"
          size="small"
          placeholder="请输入用户昵称"
          style="width:200px"
          @keyup.enter.native="listOnlineUsers"
        />
        <el-button
          type="primary"
          size="small"
          icon="el-icon-search"
          style="margin-left:1rem"
          @click="listOnlineUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table v-loading="loading" :data="userList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="avatar" label="头像" align="center" width="100">
        <template v-slot="scope">
          <img :src="scope.row.avatar" width="40" height="40" />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" align="center" />
      <el-table-column prop="ipAddr" label="ip地址" align="center" />
      <el-table-column prop="ipSource" label="登录地址" align="center" />
      <el-table-column
        prop="browser"
        label="浏览器"
        align="center"
        width="200"
      />
      <el-table-column prop="os" label="操作系统" align="center" />
      <el-table-column
        prop="lastLoginTime"
        label="登录时间"
        align="center"
        width="200"
      >
        <template v-slot="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.lastLoginTime | dateTime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template v-slot="scope">
          <el-popconfirm
            title="确定下线吗？"
            style="margin-left:10px"
            @confirm="removeOnlineUser(scope.row)"
          >
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete" /> 下线
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
      class="pagination-container"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      :page-sizes="[10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>

<script>
export default {
  created() {
    this.listOnlineUsers();
  },
  data() {
    return {
      loading: true,
      userList: [],
      keywords: null,
      current: 1,
      size: 10,
      count: 0,
      isCheck: false,
      optLog: {}
    };
  },
  methods: {
    listOnlineUsers() {
      this.loading = true;
      let params = {
        current: this.current,
        size: this.size
      };
      
      if (this.keywords) {
        params.keywords = this.keywords;
      }
      
      this.axios
        .get("/api/admin/users/online", {
          params: params
        })
        .then(({ data }) => {
          // 处理可能的不同数据结构
          if (data.data && data.data.recordList) {
            this.userList = data.data.recordList;
            this.count = data.data.count;
          } else if (Array.isArray(data.data)) {
            // 如果直接返回数组
            this.userList = data.data;
            this.count = data.data.length;
          } else if (data.recordList) {
            // 如果数据在顶层
            this.userList = data.recordList;
            this.count = data.count;
          } else {
            // 备选方案，防止数据结构不一致
            this.userList = [];
            this.count = 0;
            console.error("在线用户数据结构不符合预期", data);
          }
          this.loading = false;
        })
        .catch(error => {
          console.error("获取在线用户列表失败", error);
          this.userList = [];
          this.count = 0;
          this.loading = false;
          this.$notify.error({
            title: "失败",
            message: "获取在线用户列表失败"
          });
        });
    },
    check(optLog) {
      this.optLog = JSON.parse(JSON.stringify(optLog));
      this.isCheck = true;
    },
    removeOnlineUser(user) {
      this.axios
        .delete("/api/admin/users/online/" + user.userInfoId)
        .then(({ data }) => {
          if (data.flag) {
            this.$notify.success({
              title: "成功",
              message: data.message
            });
            this.listOnlineUsers();
          } else {
            this.$notify.error({
              title: "失败",
              message: data.message
            });
          }
        })
        .catch(error => {
          console.error("下线用户失败", error);
          this.$notify.error({
            title: "失败",
            message: "下线用户失败"
          });
        });
    },
    handleSizeChange(val) {
      this.size = val;
      this.current = 1;
      this.listOnlineUsers();
    },
    handleCurrentChange(val) {
      this.current = val;
      this.listOnlineUsers();
    }
  },
  computed: {
    tagType() {
      return function(type) {
        switch (type) {
          case "GET":
            return "";
          case "POST":
            return "success";
          case "PUT":
            return "warning";
          case "DELETE":
            return "danger";
        }
      };
    }
  }
};
</script>

<style scoped>
label {
  font-weight: bold !important;
}
</style>
