importResource("/admin/css/common.css");
importResource("/admin/css/common.css");
importResource("/plugins/font-awesome/4.7.0/css/font-awesome.css");
var nowTemplateId, nowTabId;
var templateUtils = {
    string: {
        //下划线转驼峰
        ul2ca: function (str) {
            var re = /-(\w)/g;
            return str.replace(re, function ($0, $1) {
                return $1.toUpperCase();
            });
        }
    }
};

importMiniui(function () {
    require(["miniui-tools", "message"], function (tools, message) {
        require(["text!template.json"], function (json) {
            json = JSON.parse(json);
            initTemplate(json);
        });

        var templateMap = {};

        importResource('/plugins/miniui/themes/bootstrap/skin.css');

        function createButton(template) {
            templateMap[template.id] = template;
            var html = $("<a  onclick=\"loadTable('" + template.id + "')\" >").css({
                "width": "90%",
                "height": "60px",
                "line-height": "60px",
                "margin": "5px",
                "font-size": "25px"
            }).attr("id", "button-" + template.id)
                .text(template.name)
                .addClass("mini-button")
                .addClass("template-button")
                .addClass("mini-button-primary");
            $(".template-container").append(html);
        }

        function initTemplate(templateList) {
            $(".template-container").html("");
            $(templateList).each(function () {
                createButton(this);
            });
            mini.parse();

            loadTable(templateList[0].id);
            $(".add-template").on("click", function () {
                tools.openWindow("admin/code-generator/template.html", "编辑模板", "80%", "80%", function (onClose) {
                    if (typeof onClose === 'object') {
                        if (!templateMap[onClose.id]) {
                            templateList.push(onClose);
                            createButton(onClose);
                            mini.parse();
                        }
                        templateMap[onClose.id] = onClose;
                        loadTable(onClose.id);
                    }
                });
            });
            $(".edit-template").on("click", function () {
                tools.openWindow("admin/code-generator/template.html", "编辑模板", "90%", "90%", function (onClose) {
                    if (typeof onClose === 'object') {
                        if (!templateMap[onClose.id]) {
                            templateList.push(onClose);
                            createButton(onClose);
                            mini.parse();
                        }
                        templateMap[onClose.id] = onClose;
                        loadTable(onClose.id,true);
                    }
                }, function () {
                    var iframe = this.getIFrameEl();
                    var win = iframe.contentWindow;

                    function init() {
                        if (win) {
                            win.onload = function (bind) {
                                bind.setTemplate(mini.clone(templateMap[nowTemplateId]));
                            };
                        }
                    }

                    init();
                    $(iframe).on("load", init);
                });
            });
            $(".copy-template").on("click", function () {
                message.prompt("复制模板", "请输入新的模板id", function (id) {
                    if (!id) {
                        message.showTips("请输入id", "danger");
                        return false;
                    }
                    tools.openWindow("admin/code-generator/template.html", "编辑模板", "90%", "90%", function (onClose) {
                        if (typeof onClose === 'object') {
                            if (!templateMap[onClose.id]) {
                                templateList.push(onClose);
                                createButton(onClose);
                                mini.parse();
                            }
                            templateMap[onClose.id] = onClose;
                            loadTable(onClose.id, true);
                        }
                    }, function () {
                        var iframe = this.getIFrameEl();
                        var win = iframe.contentWindow;

                        function init() {
                            if (win) {
                                win.onload = function (bind) {
                                    var newTemp = mini.clone(templateMap[nowTemplateId]);
                                    newTemp.id = id;
                                    bind.setTemplate(newTemp, true);
                                };
                            }
                        }
                        init();
                        $(iframe).on("load", init);
                    });
                });

            });
        }

        window.loadTable = function (id, reload) {
            if (!reload && nowTemplateId === id) {
                return;
            }
            var tmp = templateMap[id];

            $(".template-button").each(function () {
                mini.get(this.id).setChecked(false);
            });
            initTable(tmp);
            mini.get("button-" + id).setChecked(true);
            mini.get("button-" + id).setText(tmp.name);
            nowTemplateId = id;
        };

        function addRow(row) {
            var grid = mini.get(nowTabId + "-grid");
            if (grid) {
                if (grid.addNode) {
                    grid.addNode(row);
                } else if (grid.addRow) {
                    grid.addRow(row, -1);
                }
            }
        }

        function initTable(obj) {
            if (obj.script) {
                try {
                    eval(obj.script);
                } catch (e) {
                    console.error(e);
                }
            }
            var tables = obj.tables;
            var tabs = mini.get("code-tabs");
            tabs.removeAll();
            tabs.on("activechanged", function (e) {
                if (e.tab)
                    nowTabId = e.tab.id;
            });
            $(tables).each(function () {
                var tab = {title: this.name, id: this.var};
                tab = tabs.addTab(tab);
                var el = tabs.getTabBodyEl(tab);
                el.innerHTML = createGridHtml(this);
            });
            var height = obj.vars.length * 20 + 150;

            $("#var-container").html("").append(createSettingForm());
            mini.parse();
            mini.get("var-window").setHeight(height);
            mini.get("result").on('drawnode', function (e) {
                var node = e.node;
                if (node.type === 'dir') {
                    e.iconCls = "fa fa-folder" + (node.expanded ? "-open" : "");
                } else {
                    e.iconCls = "fa fa-file-code-o";
                }
            });
            $(tables).each(function () {
                var columns = createTableColumn(this);
                var grid = mini.get(this.var + "-grid");
                grid.set({
                    columns: columns
                });
            });
            tabs.activeTab(0);

            $(".add-column-button")
                .unbind("click")
                .on("click", function () {
                    addRow({});
                });
            $(".nex-step").unbind("click").on("click", function () {
                mini.get("var-window").show();
            });
            $(".do-generate").unbind("click").on("click", doGenerateCode);

            function createGridHtml(table) {
                var id = table.var;
                if (table.type === 'tree') {
                    return "<div id=\"" + id + "-grid\" class=\"mini-treegrid\"\n" +
                        "style=\"width:100%;height:100%;margin: auto;\"\n" +
                        "idField=\"id\"  allowCellEdit=\"true\" parentField=\"parentId\" resultAsTree=\"false\"\n" +
                        "iconField=\"icon\" allowCellValid=\"true\" treeColumn=\"index\" checkRecursive=\"true\"\n" +
                        "allowResize=\"false\" expandOnLoad=\"true\" allowCellSelect=\"true\"\n" +
                        "allowDrag=\"true\"  showPager='false' editNextOnEnterKey=\"true\" editNextRowCell=\"true\" allowDrop=\"true\" allowLeafDropIn=\"true\">\n" +
                        "</div>";
                } else {
                    return "<div id=\"" + id + "-grid\" class=\"mini-datagrid\"\n" +
                        "allowCellEdit=\"true\" showPager='false' style=\"width:100%;height:100%;margin: auto;\" editNextOnEnterKey=\"true\" editNextRowCell=\"true\"" +
                        "allowCellSelect=\"true\" allowResize=\"false\" expandOnLoad=\"true\" >" +
                        "</div>";
                }
            }

            function createTableColumn(table) {
                var columns = table.columns;
                var gridColumns = [];
                if (table.type === 'tree') {
                    gridColumns.push({
                        name: "index", type: "indexcolumn", header: "#", width: 20
                    })
                }
                $(columns).each(function () {
                    var column = this;
                    var gridColumn = mini.clone(column);
                    if (!gridColumn.headerAlign) {
                        gridColumn.headerAlign = "center";
                    }
                    if (!gridColumn.editor) {
                        gridColumn.editor = {type: "textbox"};
                    }
                    var rendererScript = gridColumn.renderer;

                    gridColumn.renderer = function (e) {
                        var row = e.record;
                        if (rendererScript) {
                            try {
                                var script = "(function(){" +
                                    "return function(row,e,value){" + rendererScript + ";};" +
                                    "})()";
                                return eval(script)(row, e, e.value ? e.value : "");
                            } catch (ex) {
                                console.error(script, ex);
                                return e.value;
                            }
                        }
                        return e.value;
                    };
                    gridColumns.push(gridColumn);
                });
                gridColumns.push({
                    name: "action", headerAlign: "center", type: "indexcolumn", header: "操作", renderer: function (e) {
                        var row = e.record;
                        return tools.createActionButton("删除", "icon-remove", function () {
                            if (e.sender.removeNode) {
                                e.sender.removeNode(row);
                            } else if (e.sender.removeRow) {
                                e.sender.removeRow(row);
                            }
                        })
                    }
                });
                return gridColumns;
            }

            function createSettingForm() {
                var vars = obj.vars;
                var table = $("<table id='" + obj.id + "-form' class='var-table'>");

                function createEditorHtml(o) {
                    var html = $("<input>");
                    if (!o.editor) {
                        html.addClass("mini-textbox");
                    } else {
                        var editor = o.editor;
                        if (typeof editor === 'string') {
                            editor = JSON.parse(editor);
                        }
                        if (o.editor) {
                            html.attr(editor);
                        }
                    }
                    html.css({"width": "100%"});
                    if (o.defaultValue) {
                        html.val(o.defaultValue);
                    }
                    html.attr("name", o.var);
                    return html[0].outerHTML;
                }

                var html = ["<tr>"];
                var index = 0;
                $(vars).each(function () {
                    if (index !== 0 && index % 2 === 0) {
                        html.push("</tr>");
                    }
                    html.push("<td class='title'>");
                    html.push(this.name);
                    html.push("</td>");
                    html.push("<td class='editor'>");
                    html.push(createEditorHtml(this));
                    html.push("</td>");
                    index++;
                });
                html.push("<tr/>");
                return table.append(html.join(""));
            }
        }

        function doGenerateCode() {
            var template = templateMap[nowTemplateId];
            var tabs = mini.get("code-tabs").getTabs();
            var formData = tools.getFormData("#" + nowTemplateId + "-form", true);
            if (!formData) {
                return;
            }
            var vars = formData;
            $(tabs).each(function () {
                var gridId = this.id + "-grid";
                var gridData = mini.get(gridId).getData();
                vars[this.id] = gridData;
            });
            renderTemplate(vars, template.templates, function (result) {
                var resultTree = mini.get("result");
                resultTree.loadData(result);
                console.log(result);
            });
        }

        function renderTemplate(vars, template, call) {
            require(["art-template"], function (art) {
                for (var util in templateUtils) {
                    art.defaults.imports[util] = templateUtils[util];
                }

                function doRender(temp) {
                    if (temp.template) {
                        temp.template = art.render(temp.template, vars);
                    }
                    if (temp.file) {
                        temp.file = art.render(temp.file, vars);
                    }
                    if (temp.children) {
                        $(temp.children).each(function () {
                            doRender(this);
                        });
                    }
                }

                var target = mini.clone(template);
                $(target).each(function () {
                    doRender(this);
                });
                call(target);
            });
        }
    });
});
