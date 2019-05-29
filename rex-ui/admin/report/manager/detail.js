importResource("/admin/css/common.css");
importResource("/plugins/font-awesome/4.7.0/css/font-awesome.css");
importResource("/admin/report/jquery-rebox.css");
var buttons = [];

importMiniui(function () {
    require(["plugin/jquery/jquery-rebox"]);

    function initButtons() {
        var index = 0;
        var html = [];
        $(buttons).each(function () {
            if (index !== 0 && index % 5 === 0) {
                html.push("<br>")
            }
            html.push('<a id="check_button_' + (this.value) + '" readOnly="true" enabled="false" class="mini-button" checkOnClick="true" style="margin: 5px;width: 150px;height: 40px;line-height: 40px;font-size: 14px">' +
                this.text +
                '</a>');
            index++;
        });
        $(".buttons").html(html.join(""));
    }

    initButtons();

    importResource('/plugins/miniui/themes/bootstrap/skin.css');


    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        window.request = request;
        window.message = message;
        window.tools = tools;
        request.get("dict/all/check-fail-reason", function (resp) {
            if (resp.status === 200) {
                var list = [];
                for (var value in resp.result) {
                    list.push({text: resp.result[value], value: value});
                }
                buttons = list;
                initButtons();
                mini.parse();
            }
        });
        mini.parse();
        function loadIllegalCode(data, index) {

            mini.get("illegalCode" + (index ? "_" + index : "")).setValue(data.id);
            mini.get("illegalCode" + (index ? "_" + index : "")).setText(data.id);
            mini.get("illegalCode" + (index ? "_" + index : "")).setIsValid(true);
            mini.get("illegalFine" + (index ? "_" + index : "")).setValue(data.defaultMoney);
            mini.get("illegalScore" + (index ? "_" + index : "")).setValue(data.illegalScore);
            mini.get("illegalContent" + (index ? "_" + index : "")).setValue(data.illegalContent);
            mini.get("punishBasis" + (index ? "_" + index : "")).setValue(data.punishBasis);
        }


        mini.get("illegalCode").on("enter", function () {
            var code = mini.get("illegalCode").getText();
            var box = message.loading("加载违法代码中...");

            request.get("illegal/code/by-code/" + code, function (resp) {
                box.hide();
                if (resp.status === 200 && resp.result) {
                    loadIllegalCode(resp.result);
                } else if (!resp.result) {
                    message.showTips("违法代码不存在");
                } else {
                    message.showTips("加载违法代码失败");
                }

            })
        });

        mini.get("illegalCode").on("buttonclick", function () {
            tools.openWindow("admin/illegal/code/select.html", "选择违法代码", "800", "600", function (data) {
                if (data && typeof data === 'object') {
                    loadIllegalCode(data);
                    //console.log(data);
                }
            })
        });

        $(".back").on("click", function () {
            tools.closeWindow("close");
        });

        var id = request.getParameter("id");
        var plateType = request.getParameter("plateType");
        var plateNumber = request.getParameter("plateNumber");
        if (id) {
            loadData();
        }
        function loadData() {
            var box = message.loading("加载数据中...");
            request.get("illegal/report/checked/" + id + "/" + plateType + "/" + encodeURI(plateNumber), function (resp) {
                box.hide();
                if (resp && resp.status === 200) {
                    setFormData(resp.result)

                    //if(resp.result.)
                } else {
                    message.alert("加载数据失败:" + resp.message);
                }
            })
        }

        function createIllegalCodeHtml(index) {
            var illegalCodeHtml = "<table style=\"width: 800px\" panelName='panel_" + index + "' class='illegalCodeForm' id='illegalCodeForm_" + index + "'>\n" +
                "      <tr>\n" +
                "      <td>违法代码:</td>\n" +
                "      <td><input id=\"illegalCode_" + index + "\" name=\"illegalBehavior\" required\n" +
                "                 class=\"mini-buttonedit\"/></td>\n" +
                "      <td>罚款金额:</td>\n" +
                "      <td><input id=\"illegalFine_" + index + "\" name='illegalFine' required vtype=\"int\"\n" +
                "                 class=\"mini-textbox\"/></td>\n" +
                "      <td>违法记分:</td>\n" +
                "      <td><input id=\"illegalScore_" + index + "\" name='illegalScore' required vtype=\"int\"\n" +
                "                 class=\"mini-textbox\"/></td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "      <td>违法内容:</td>\n" +
                "      <td colspan=\"5\"><input name='illegalContent' id=\"illegalContent_" + index + "\" readonly\n" +
                "                             class=\"mini-textbox\" style=\"width: 100%\"/>\n" +
                "      </td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "      <td>处罚依据:</td>\n" +
                "      <td colspan=\"5\"><input name='punishBasis'  id=\"punishBasis_" + index + "\" readonly\n" +
                "                             class=\"mini-textarea\"\n" +
                "                             style=\"width: 100%\"/>\n" +
                "      </td>\n" +
                "  </tr>\n" +
                " </table>";

            return illegalCodeHtml;
        }

        var illegalCodePanel = mini.get("illegalCodeInfo");

        illegalCodePanel.on("closeclick", updateIndex);

        function updateIndex() {

            $(illegalCodePanel.getTabs()).each(function (i, o) {
                illegalCodePanel.updateTab(o, {title: "违法行为" + (i + 1)});
            })

        }

        function addIllegalCodePanel() {
            var index = illegalCodePanel.getTabs().length;
            index++;
            //add tab
            var tab = {name: "panel_" + index, title: "违法行为" + (index), showCloseButton: true};
            tab = illegalCodePanel.addTab(tab);

            //tab body
            var el = illegalCodePanel.getTabBodyEl(tab);

            el.innerHTML = createIllegalCodeHtml(index);

            //active tab
            illegalCodePanel.activeTab(tab);
            mini.parse();
            mini.get("illegalCode_" + index).on("buttonclick", function () {
                tools.openWindow("admin/illegal/code/select.html", "选择违法代码", "800", "600", function (data) {
                    if (data && typeof data === 'object') {
                        loadIllegalCode(data, index);
                        //console.log(data);
                    }
                })
            });

            return "illegalCodeForm_" + (index);
        }

        //$(".add-illegal-code").on("click", addIllegalCodePanel);

        // $(".submitOk").on("click", function () {
        //     var formData = tools.getFormData("#okForm", true);
        //     if (!formData) {
        //         return;
        //     }
        //
        //     var validate = true;
        //     var illegalInfo = [];
        //
        //     $(".illegalCodeForm").each(function () {
        //         var form = $(this);
        //         var data = tools.getFormData("#" + form.attr("id"), true);
        //         var panelName = form.attr("panelName");
        //
        //         if (!data) {
        //             validate = false;
        //             var tab = illegalCodePanel.getTab(panelName);
        //             illegalCodePanel.activeTab(tab);
        //             return null;
        //         }
        //         for (var f in formData) {
        //             data[f] = formData[f];
        //         }
        //         illegalInfo.push(data);
        //     });
        //     if (!validate) {
        //         return;
        //     }
        //     var req = {
        //         illegalCar: {
        //             videoShotPath: getImages()
        //         },
        //         illegalInfo: illegalInfo
        //     };
        //
        //     message.confirm("确定复核此举报", function () {
        //         var box = message.loading("提交中...");
        //         request.post("illegal/report/check/" + id + "/" + plateType + "/" + plateNumber + "/ok", req, function (resp) {
        //             box.hide();
        //             if (resp.status === 200) {
        //                 //tools.closeWindow("ok");
        //                 message.confirm("复核成功!", tools.closeWindow, tools.closeWindow);
        //             } else {
        //                 message.showTips("复核失败:" + resp.message, "danger");
        //             }
        //         })
        //     });
        // });


        // $(".submitFail").on("click", function () {
        //     var checkList = [];
        //     for (var i = 0; i < buttons.length; i++) {
        //         var b = buttons[i];
        //         var el = mini.get("check_button_" + i);
        //         if (el.checked) {
        //             checkList.push(b.value);
        //         }
        //     }
        //     message.confirm("确定取消此举报", function () {
        //         var box = message.loading("提交中...");
        //         request.post("illegal/report/check/" + id + "/" + plateType + "/" + plateNumber + "/fail", checkList, function (resp) {
        //             box.hide();
        //             if (resp.status === 200) {
        //                 //tools.closeWindow("ok");
        //                 message.confirm("取消成功!", tools.closeWindow, tools.closeWindow);
        //             } else {
        //                 message.showTips("取消失败:" + resp.message, "danger");
        //             }
        //         })
        //     });
        // });
        function initDis() {
            var adminDivision = mini.getbyName("adminDivision");
            var road = mini.get("road");
            var roadSeg = mini.getbyName("roadCode");

            function drawCell(e) {
                var item = e.record, field = e.field, value = e.value;
                //组织HTML设置给cellHtml
                e.cellHtml = "<span>" + item.code + "</span>&nbsp;" + '<span>' + item.name + '</span>';
            }

            function refactorText(list) {
                $(list).each(function () {
                    this.text = this.code + " " + this.name;
                });
                return list;
            }

            adminDivision.on("drawcell", drawCell);
            road.on("drawcell", drawCell);
            roadSeg.on("drawcell", drawCell);


            request.createQuery("district/all").exec(function (resp) {
                if (resp.status === 200) {
                    adminDivision.setData(refactorText(resp.result));
                }
            });
            adminDivision.on("valuechanged", function (e) {
                var dist = e.selected;
                if (dist) {
                    var loading = message.loading("加载道路信息...");
                    request.createQuery("road/dist-code/" + dist.code).exec(function (resp) {
                        loading.hide();
                        if (resp.status === 200) {
                            refactorText(resp.result);
                            road.setData(resp.result);
                        } else {
                            message.alert(resp.message);
                        }
                    });
                }
            });
            road.on("valuechanged", function (e) {
                var road = e.selected;
                if (road) {
                    var loading = message.loading("加载路段信息...");
                    request.createQuery("road-seg/road-code/" + road.code)
                        .noPaging().exec(function (resp) {
                        loading.hide();
                        if (resp.status === 200) {
                            if (resp.result.length === 0) {

                                roadSeg.setData(refactorText([mini.clone(road)]));
                            } else {
                                roadSeg.setData(refactorText(resp.result));
                            }
                            roadSeg.select(0);
                        } else {
                            message.alert(resp.message);
                        }
                    });
                }
            });
        }

        // createMapAndMark();
        //  initVideoPlayer("http://jtzzlm.cdjg.gov.cn/upload/videos/wf/2017-11-02/15096172778804.mov");
        function appendImage(urls) {
            $(urls).each(function () {
                var div = $("<div style='margin: 5px'>");
                div.addClass("img-item");
                var a= $("<a>").attr("href",this);
                a.append($("<img >").attr("src", this).css({
                    "width": 160,
                    "height": 150
                }));
                div.append(a);

                // div.append("<span class='del-btn'>" +
                //     "<span class='fa-stack fa-sm'>"+
                // "<i class='fa fa-circle fa-stack-2x'></i>"+
                //     "<i class='fa fa-times fa-stack-1x fa-inverse'></i>"+
                //    " </span>" +
                //     "</span>");

                $(".screenshot-list").append(div);
            });
            $('.screenshot-list').rebox({ selector: 'a' });
            $('.del-btn').unbind("click").on('click',function(){
                $(this).parent().remove();
            })
        }

        function getImages() {
            var list = [];
            $(".screenshot-list img").each(function () {
                list.push($(this).attr("src"));
            });
            return list;
        }

        function initPlayer(location, ys) {
            var videoUrl = location.indexOf("http") === -1 ? ("http://jtzzlm.cdjg.gov.cn" + location) : location;

            initVideoPlayer(videoUrl, function (player) {
                var videoPanel = mini.get("videoPanel");
                var nowRotation = 0;
                var cu_time = -1;

                videoPanel.setButtons([
                    {html: '<i class="fa fa-rotate-left rotate-left em mini-panel-title" style="cursor: pointer" aria-hidden="true">向左旋转</i>&nbsp;'},

                    {html: '<i class="fa fa-rotate-right rotate-right em mini-panel-title" style="cursor: pointer" aria-hidden="true">向右旋转</i>&nbsp;'},

                    // {html: '<i class="fa fa-file-image-o screenshot em mini-panel-title" style="cursor: pointer" aria-hidden="true">截图</i>&nbsp;'},

                    {html: '<a style="color: white" target="_blank" href="' + videoUrl + '"><i class="fa fa-cloud-download download-video em mini-panel-title" style="cursor: pointer" aria-hidden="true">下载视频</i></a>'}
                ]);
                window.timeHandler = function (time) {
                    console.log(time);
                    cu_time = time;
                };

                player.addListener('time', 'timeHandler');

                $(".rotate-left").unbind("click").on("click", function () {
                    nowRotation -= 90;
                    if (nowRotation <= -360) {
                        nowRotation = 0;
                    }

                    player.videoRotation(nowRotation);
                });

                $(".rotate-right").unbind("click").on("click", function () {
                    nowRotation += 90;
                    if (nowRotation >= 360) {
                        nowRotation = 0;
                    }

                    player.videoRotation(nowRotation);
                });
                $(".convert").unbind("click").on("click", function () {
                    var loading = message.loading("转码中,请稍候...")
                    request.get("video/convert/m3u8", {location: ys}, function (e) {
                        loading.hide();
                        if (e.status === 200) {
                            initPlayer(e.result, ys);
                        } else {
                            mini.alert(e.message);
                        }
                    });
                });
                $(".screenshot").unbind("click").on("click", function () {
                    //  var loading = message.loading("截图中,请稍候...");
                    player.screenshot('video', true, '视频截图');

                    // if (cu_time === -1) {
                    //     cu_time = Math.random() * 10;
                    // }
                    // var seconds = Math.round(cu_time);
                    // request.get("video/screenshot", {rotate:nowRotation,location: ys, secondsJson: JSON.stringify([seconds])}, function (e) {
                    //     loading.hide();
                    //     if (e.status === 200) {
                    //         appendImage(e.result);
                    //     } else {
                    //         mini.alert(e.message);
                    //     }
                    //     cu_time = -1;
                    // });
                })
            });

        }
        function setFormData(data) {
            mini.get("log-grid").setData(data.reportLogs);
            mini.get("log-grid").getColumn("status").renderer = function (e) {
                return e.value === 1 ? "通过" : "未通过";
            };
            appendImage(data.illegalCar.videoShotPath);
            function setReadOnly(form) {
                var fields = form.getFields();
                //$(conf.target + " .mini-button").hide();
                for (var i = 0, l = fields.length; i < l; i++) {
                    var c = fields[i];
                    if (c.setRequired) c.setRequired(false);
                    if (c.setReadOnly) c.setReadOnly(true);     //只读
                    if (c.setIsValid) c.setIsValid(true);      //去除错误提示
                    if (c.addCls) c.addCls("asLabel");          //增加asLabel外观
                }
            }

            if (data.reportInfo.videoUrl) {
                initPlayer(data.reportInfo.videoUrl, data.reportInfo.videoUrl);
            }
            createMapAndMark(data.reportInfo.latitude, data.reportInfo.longitude);

            mini.getbyName("illegaltime").setValue(data.reportInfo.reportDate);

            $("[field]").each(function () {

                var el = $(this);
                var field = el.attr("field");
                if (field) {
                    try {
                        var value = eval("(function(){return function(d){return d." + field + ";}})()")(data);
                        if (value) {
                            el.text(value);
                        } else {

                        }
                    } catch (e) {
                        console.error(e);
                    }
                }
            });

            var tabs = mini.get("tabs1");

            if (data.illegalCar.signReviewStatus == '1102') {
                tabs.removeTab(tabs.getTab("illegalAction"));
                if (data.illegalCar.stopCause) {
// /check_button_
                    $(data.illegalCar.stopCause.split(",")).each(function () {
                        var bt = mini.get("check_button_" + this);
                        if (bt) {
                            bt.setChecked(true);
                            bt.setEnabled(true);
                        }
                    })
                }
            } else {
                initDis();
                tabs.removeTab(tabs.getTab("checkFailPanel"));
                //加载
                var formData = new mini.Form("#okForm");
                setReadOnly(formData);
                var illegalInfo = data.illegalInfo[0];

                formData.setData(illegalInfo);
                request.createQuery("road-seg/code/" + illegalInfo.roadCode).exec(function (resp) {
                    if (resp.status === 200) {
                        var roadCode;

                        if (resp.result) {
                            roadCode = resp.result.roadCode;
                            mini.getbyName("roadCode").setValue(resp.result.code);
                            mini.getbyName("roadCode").setText(resp.result.code + " " + resp.result.name);
                        } else {
                            roadCode = illegalInfo.roadCode;
                        }
                        request.createQuery("road/code/" + roadCode).exec(function (respR) {
                            if (respR.status === 200) {
                                mini.get("road").setValue(respR.result.code);
                                mini.get("road").setText(respR.result.code + " " + respR.result.name);
                                if (illegalInfo.roadCode === roadCode) {
                                    mini.getbyName("roadCode").setValue(respR.result.code);
                                    mini.getbyName("roadCode").setText(respR.result.code + " " + respR.result.name);
                                }
                            }
                        });
                    }
                });

                function loadCode(code, call) {
                    request.get("illegal/code/by-code/" + code, function (resp) {
                        if (resp.status === 200 && resp.result) {
                            call(resp.result);
                        } else if (!resp.result) {
                            message.showTips("违法代码不存在");
                        } else {
                            message.showTips("加载违法代码失败");
                        }
                    })

                }


                $(data.illegalInfo).each(function (i, d) {
                    var form;
                    if (i === 0) {
                        form = new mini.Form("#illegalCodeForm");
                    } else {
                        var formId = addIllegalCodePanel();
                        form = new mini.Form("#" + formId);
                    }
                    setIllegalCode(d, i === 0 ? null : i + 1, form);

                    function setIllegalCode(illegal, index, f) {
                        loadCode(illegal.illegalBehavior, function (data) {
                            illegal.illegalContent = data.illegalContent
                            illegal.punishBasis = data.punishBasis
                            mini.get("illegalCode" + (index ? "_" + index : "")).setValue(data.id);
                            mini.get("illegalCode" + (index ? "_" + index : "")).setText(data.id);
                            mini.get("illegalCode" + (index ? "_" + index : "")).setIsValid(true);

                            f.setData(illegal);
                            setReadOnly(f);
                        });
                    }

                });
            }

        }

        function createMapAndMark(longitude, latitude) {
            var iframe = $("<iframe border='0' contentBorder='0'>");
            iframe.css({"width": "100%", "height": "100%", "border": "0px"});

            iframe.attr("src", BASE_PATH + "admin/map/map-marker.html");
            $(".illegalAddressMap").append(iframe);
            iframe.on("load", function () {
                var win = this.contentWindow;
                if (win) {
                    win.showAreaMarker("成都市", [
                        [longitude, latitude, ""]
                    ], true);
                }
                // console.log(win);
            });

        }

        function initVideoPlayer(video, call) {
            $(".illegalVideoPlayer").html("");
            var iframe = $("<iframe border='0' contentBorder='0'>");
            iframe.css({"width": "100%", "height": "100%", "border": "0px"});

            iframe.attr("src", BASE_PATH + "admin/video/index.html");
            $(".illegalVideoPlayer").append(iframe);

            iframe.on("load", function () {
                var win = this.contentWindow;
                if (win) {
                    win.init(video, null, call);
                }
                // console.log(win);
            });
        }
    });

});
