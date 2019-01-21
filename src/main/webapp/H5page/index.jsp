<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <title></title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%--<link rel="stylesheet" href="font-awesome.min.css">--%>
    <link rel="stylesheet" href="http://${header["Host"]}${pageContext.request.contextPath}/H5page/font-awesome.min.css">
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <!-- 引入样式 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vant@1.4/lib/index.css">
    <!-- 引入组件 -->
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/vant@1.4/lib/vant.min.js"></script>
    <style>
        p {
            margin: 0 0 0.3125rem;
        }

        .flex {
            display: flex;
            justify-content: space-between;
        }

        .textL {
            text-align: left;
            word-wrap: break-word;
        }

        .textC {
            text-align: center;
            color: #999;
        }

        .block-item {
            border-bottom: 2px solid #eee;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 15px 10px;
        }

        .text-hidden {
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }

        .colorRed {
            color: red;
        }

        .colorGreen {
            color: green;
        }

        .trans-item {
            display: flex;
            align-items: center;
            margin-bottom: 6px;
        }

        .trans-item span {
            font-size: 12px;
            margin: 0 3px;
        }

        .van-circle__text {
            font-size: 14px;
        }

        .block-wrap {
            /* width: 90%; */
            /* margin: 0 auto;
            border: 1px solid transparent;
            border-color: #bce8f1 */
        }

        .main-title {
            color: #31708f;
            background-color: #d9edf7;
            border-color: #bce8f1;
            padding: 10px;
            margin-bottom: 0;
        }

        .left-title {
            font-weight: bold;
            font-size: 13px;
            line-height: 1.6;
            color: #333333;
        }

        .left-value {
            font-size: 14px;
            line-height: 1.6;
            color: #333333;
            word-wrap: break-word;
        }

        .first-div {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .blue-color {
            color: #3498db
        }

        .address {
            word-wrap: break-word;
            /* border-bottom: 1px solid #f0f0f0; */
            font-size: 14px;
            line-height: 1.4;
            margin-bottom: 0;
            color: #999;
            text-align: center;
            /* padding-bottom: 20px */
        }

        .second-div {
            width: 90%;
            margin: 0 auto;
            border: 1px solid transparent;
            border-top: 2px solid transparent;
            border-color: #3498db;
        }
    </style>
</head>

<body>
    <div id="app">
        <van-pull-refresh v-model="isLoading" @refresh="onRefresh" loading-text="Data loading..." pulling-text="Drop down to refresh..."
            loosing-text="Release can be refreshed...">
            <!-- <p style="text-align:left;font-size: 24px;padding:30px 0 5px 5%;color: #333333">BlockChain-Scan.Info</p>
            <div style="margin-bottom:20px;border-bottom:1px solid #f0f0f0;padding-bottom: 20px">
                <p style="font-size:20px;color:#777;text-align:center;padding: 20px 0; border-top: 1px solid #f0f0f0;">Address</p>
                <p class="address" v-for="(add,addIndex) in addressArr">{{add}}<span v-if="addIndex != addressArr.length-1">;</span></p>
            </div> -->
            <div v-if="hasData" class="block-wrap">

                <div class="second-div">
                    <p class="main-title">Transaction Information</p>
                    <!-- <van-cell-group>
                    <van-field placeholder="Please Enter The Data" v-model="iptVal">
                        <van-button slot="button" size="small" type="primary" @click="searchData">Search data</van-button>
                    </van-field>
                </van-cell-group> -->
                    <van-list v-model="loading" :finished="finished" finished-text="No more data" @load="pushArr"
                        loading-text="Data loading..." :offset="offset">
                        <div class="block-item" v-for="(list,index) in listArr" :key="index">
                            <div class="first-div">
                                <div>
                                    <p class="left-title">BlockNumber:</p>
                                    <p class="left-value">{{list.blockNumber}}</p>
                                </div>
                                <div>
                                    <p class="left-title">&nbsp;</p>
                                    <p class="left-value">{{list.timestamp | timeFilter}}</p>
                                </div>
                            </div>
                            <div>
                                <p class="left-title">BlockHash:</p>
                                <p class="left-value">{{list.blockHash}}</p>
                            </div>
                            <!-- <p class="flex">
                        <span style="font-weight:600">{{list.blockNumber}}</span>
                        <span style="font-size:13px">{{list.timestamp | timeFilter}}</span>
                    </p> -->
                            <!-- <p class="textL" style="font-weight:500">{{list.blockHash}}</p> -->

                            <div style="display:flex;align-items:center;justify-content: center">
                                <van-circle v-model="list.sid" :rate="list.id" :speed="100" layer-color="#ebedf0" size="30px"
                                    :text="list.sid"></van-circle>
                                <ul>
                                    <li class="trans-item" v-for="(item,index) in list.transactions" :key="index">
                                        <span class="text-hidden" style="display:inline-block;max-width:50px;vertical-align: top;">{{item.statusName}}</span>
                                        <span v-if="item.from == addresses">
                                            <i class="fa fa-long-arrow-right"></i>
                                        </span>
                                        <span v-else>
                                            <i class="fa fa-long-arrow-left"></i>
                                        </span>
                                        <span v-if="item.from != addresses" class="text-hidden blue-color" style="display:inline-block;max-width:140px;vertical-align: top;margin-right:20px">{{item.from}}</span>
                                        <span v-else class="text-hidden blue-color" style="display:inline-block;max-width:140px;vertical-align: top;margin-right:20px">{{item.to}}</span>
                                        <span :class="{colorGreen : isNegative}" style="max-width:80px;word-wrap:break-word ;">{{item.data
                                            |science}}</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </van-list>
        </van-pull-refresh>
    </div>
    </div>
    <div v-else style="text-align:center">
        <img src="http://${header["Host"]}${pageContext.request.contextPath}/H5page/noData.jpg" alt="" style="margin: 100px auto;margin-bottom: 0">
        <p>暂无数据</p>
    </div>
    </div>

    <script>
        var app = new Vue({
            el: '#app',
            data: {
                // strokeWidth:100,
                nihaoArr: [0, 10, 20, 30, 40],
                loading: false,
                finished: false,
                currentRate: 0,
                isNegative: false,
                number: 11,
                rate: 120,
                ipt: false,
                iptVal: "",
                offset: 300,
                listArr: [],
                allArr: [],
                // addresses: "0xca098c0725cecf2fb45ebf78822178090e7ff6a5",
                addresses: "0xca098c0725cecf2fb45ebf78822178090e7ff6a5",
                isLoading: false,
                latestBlock: '',
                hasData: true,
                addressArr: ["0xca098c0725cecf2fb45ebf78822178090e7ff6a5", "0x8591b1c41ac0804d35fabd9b532001494aff0081"]
                // pageStart: 0,
                // pageNum: 5
            },
            filters: {
                timeFilter(timestamp) {
                    let date = new Date(timestamp * 1000);
                    let Y = date.getFullYear() + "-";
                    let M =
                        date.getMonth() + 1 < 10
                            ? "0" + (date.getMonth() + 1) + "-"
                            : date.getMonth() + 1 + "-";
                    let D =
                        date.getDate() < 10 ? "0" + date.getDate() + " " : date.getDate() + " ";
                    let h =
                        date.getHours() < 10
                            ? "0" + date.getHours() + ":"
                            : date.getHours() + ":";
                    let m =
                        date.getMinutes() < 10
                            ? "0" + date.getMinutes() + ":"
                            : date.getMinutes() + ":";
                    let s =
                        date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                    return Y + M + D + h + m + s;
                },
                science(num) {
                    // num = parseInt(num.substring(2), 16);
                    // let m = num.toExponential().match(/\d(?:\.(\d*))?e([+-]\d+)/);
                    // num = num.toFixed(Math.max(0, (m[1] || "").length - m[2]));
                    // num = parseFloat(num).toPrecision(2)
                    // return num
                    num = num / Math.pow(10, 18);
                    let m = num.toExponential().match(/\d(?:\.(\d*))?e([+-]\d+)/);
                    return num.toFixed(Math.max(0, (m[1] || "").length - m[2]));
                }
            },
            created() {
                /*if (this.get_url_json.addresses) {
                    this.addresses = this.get_url_json.id
                }*/
                this.last()
                 if("${addresses}"){
                     this.addresses = "${addresses}"
                 }
                 if("${addresses}"){
                     this.addressArr = "${addresses}".split(',')
                 }
            },
            mounted() {

            },
            methods: {
                timestampToTime(timestamp) {
                    let date = new Date(timestamp * 1000);
                    let Y = date.getFullYear() + "-";
                    let M =
                        date.getMonth() + 1 < 10
                            ? "0" + (date.getMonth() + 1) + "-"
                            : date.getMonth() + 1 + "-";
                    let D =
                        date.getDate() < 10 ? "0" + date.getDate() + " " : date.getDate() + " ";
                    let h =
                        date.getHours() < 10
                            ? "0" + date.getHours() + ":"
                            : date.getHours() + ":";
                    let m =
                        date.getMinutes() < 10
                            ? "0" + date.getMinutes() + ":"
                            : date.getMinutes() + ":";
                    let s =
                        date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                    return Y + M + D + h + m + s;
                },
                // 区块从高到低排序
                by(name) {
                    return function (o, p) {
                        var a, b;
                        if (typeof o === "object" && typeof p === "object" && o && p) {
                            a = o[name];
                            b = p[name];
                            if (a === b) {
                                return 0;
                            }
                            if (typeof a === typeof b) {
                                return b < a ? -1 : 1;
                            }
                            return typeof b < typeof a ? -1 : 1;
                        }
                        else {
                            throw ("error");
                        }
                    }
                },
                onRefresh() {
                    setTimeout(() => {
                        this.listArr.splice(0, this.listArr.length);
                        this.isLoading = false;
                        this.finished = false;
                        this.onLoad()
                        this.pushArr()
                    }, 500);
                },
                last() {
                    let _this = this
                    let ajaxJson = {
                        // url: 'http://18.179.50.113/Search-Web/App/query/latestBlock',
                        // url: 'http://192.168.0.112:8080/App/query/latestBlock',
                        url: 'http://${header["Host"]}${pageContext.request.contextPath}/App/query/latestBlock',
                        method: 'post',
                        cache: true,
                        timeout: 0,
                        dataType: 'json',
                        data: {

                        },
                        success: function (res) {
                            console.log(res);
                            if (res.status == 200) {
                                _this.latestBlock = res.data[0]
                                _this.onLoad()
                            } else {
                                _this.loading = false;
                                _this.$toast({
                                    mask: true,
                                    message: "数据请求失败,请刷新重试"
                                });
                            }
                        },
                        error: function (err) {
                            console.log(err);
                        }
                    };
                    // console.log('ajaxJson:' + JSON.stringify(ajaxJson));
                    $.ajax(ajaxJson);
                },
                onLoad() {
                    let _this = this;
                    let ajaxJson = {
                        // url: 'http://18.179.50.113/Search-Web/App/queryTransactionRecord',
                        // url: 'http://192.168.0.112:8080/App/queryTransactionRecord',
                        url: 'http://${header["Host"]}${pageContext.request.contextPath}/App/queryTransactionRecord',
                        method: 'post',
                        cache: true,
                        timeout: 0,
                        dataType: 'json',
                        data: {
                            addresseStr: _this.addresses,
                        },
                        success: function (res) {
                            console.log(res);
                            if (res.status == 200 && res.data.length != 0) {
                                // _this.loading = false;

                                _this.hasData = true;
                                console.log(res.data);
                                res.data.sort(_this.by('blockNumber'))
                                for (let i = 0; i < res.data.length; i++) {
                                    res.data[i].id = ((_this.latestBlock - res.data[i].blockNumber) * 8.3).toFixed(0)
                                    if (_this.latestBlock - res.data[i].blockNumber > 11) {
                                        res.data[i].id = 100
                                    } else {
                                        res.data[i].sid = i * 8.3
                                    }

                                }
                                // _this.listArr = res.data;
                                _this.allArr = res.data;
                                console.log(11111111111 + _this.allArr);
                            } else {
                                _this.hasData = false;
                                _this.loading = false;
                                // _this.$toast({
                                //     mask: true,
                                //     message: "数据请求失败,请刷新重试"
                                // });
                            }
                        },
                        error: function (err) {
                            console.log(err);
                        }
                    };
                    // console.log('ajaxJson:' + JSON.stringify(ajaxJson));
                    $.ajax(ajaxJson);
                },
                searchData() {
                    this.ipt = true;
                },
                pushArr() {
                    setTimeout(() => {
                        let nowLength = this.listArr.length
                        let allLength = this.allArr.length
                        if (this.listArr.length == 0) {
                            console.log(1)
                            for (let i = 0; i < this.allArr.length; i++) {
                                if (i < 2) {
                                    this.listArr.push(this.allArr[i])
                                }
                            }
                            this.loading = false;
                        } else if (this.listArr.length != this.allArr.length && this.listArr.length != 0 && this.allArr.length - this.listArr.length > 1) {
                            console.log(2)
                            this.listArr.push(this.allArr[nowLength])
                            this.listArr.push(this.allArr[nowLength + 1])
                            this.loading = false;
                        } else if (this.listArr.length != this.allArr.length && this.listArr.length != 0 && this.allArr.length - this.listArr.length <= 1) {
                            console.log(3)
                            this.listArr.push(this.allArr[nowLength])
                            this.loading = false;
                        } else {
                            console.log(4)
                            this.loading = false;
                            this.finished = true;
                        }
                    }, 1000)
                },
                //获取url传参数
                get_url_json() {
                    //获取url中"?"符后的字串
                    var url = decodeURI(window.location.search);
                    var json = {};
                    if (url.indexOf("?") != -1) {
                        //消除?号
                        var str = url.substr(1);
                        var strs = str.split("&");
                        for (var i = 0; i < strs.length; i++) {
                            json[strs[i].split("=")[0]] = strs[i].split("=")[1];
                        }
                    }
                    // console.log('url_json:' + JSON.stringify(json));
                    return json;
                }
            }
        })    
    </script>

</body>

</html>