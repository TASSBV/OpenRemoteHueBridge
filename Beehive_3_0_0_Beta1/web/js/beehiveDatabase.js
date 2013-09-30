/**
 * @author allen.wei 2009-2-12
 */

// Some constants
var VENDOR_SELECT = "vendor_select";
var MODEL_SELECT = 'model_select';
var REMOTE_SELECT = 'remote_select';
var FILTER = "Filter...";

$(document).ready(function () {
    bindInputEvent();
    bindInputResetBtnEvent();
});

/**
 * Finds html element select's option which in the <code>div</code> and it's text begin with  <code>key</code>,than mark it as selceted.
 * @param div
 * @param key
 */
function findAndSelectOption(div, key) {
    $(div).find("option:selected").removeAttr("selected");
    $(div).find("option[text^='" + key + "']:first").attr("selected", "selected").change();
}

/**
 * Binds some event to ".filter_input"
 */
function bindInputEvent() {
    $(".filter_input").focus(function() {
        $(this).val("");
    });
    $(".filter_input").blur(function() {
        $(this).val(FILTER);
    });

    $(".filter_input").keyup(function() {
        findAndSelectOption($(this).parent().next(), $(this).val());
    });
}

/**
 * Binds some event to ".reset_filter_input_btn"
 */
function bindInputResetBtnEvent() {
    $(".reset_filter_input_btn").unbind().click(function () {
        $(this).prev(".filter_input").val(FILTER);
        $(this).parent().next("div").find("option:first").attr("selected", "selected").change();
    });

}

/**
 * Binds some event to ".detail_title"
 */
function bindDetailTitleEvent() {
    $(".detail_title").click(function () {
        $(this).next(".detail_content").toggle();
    });
}

/**
 * Shows select html element
 * @param select_id
 * @param selected_value
 */
function showSelect(select_id, selected_value) {
    switch (select_id) {
        case VENDOR_SELECT:
            afterSelectVendor(selected_value)
            break;
        case MODEL_SELECT:
            afterSelectModel(selected_value)
            break;
        case REMOTE_SELECT:
            showLircDetailsByRemote(selected_value)
            break;

    }
}

/**
 * Invoked after Vendor select selected
 * @param vendor_id
 */
function afterSelectVendor(vendor_id) {
    if (vendor_id != 0) {
        myAjaxCall("index.html", {method:"showModelSelect",generateSelectTcagId:MODEL_SELECT,vendorId:vendor_id}, function (html) {
            $(".lirc_select_container").not($("#vendor_select_container")).hide();
            addSelectToPage(html, "#model_select_container");
        });
    } else {
        $("#vendor_select_container").nextAll(".lirc_select_container").hide();
        clearLircDetail();
    }
}

/**
 * Invoked after Model select selected
 * @param vendor_id
 */
function afterSelectModel(model_id) {
    if (model_id != 0) {
        myAjaxCall("index.html", {method:"isMutiSection",modelId:model_id}, function (result) {
            if (result == "true") {
                $.get("index.html", {method:"showRemoteSelect",generateSelectTcagId:REMOTE_SELECT,modelId:model_id}, function (html) {
                    $("#model_select_container").nextAll(".lirc_select_container").hide();
                    clearLircDetail();
                    addSelectToPage(html, "#section_select_container");
                });
            } else {
                showLircDetailByModel(model_id);
            }
        });
    } else {
        if ($("#model_select_container").nextAll(".lirc_select_container").size() > 0) {
            $("#model_select_container").nextAll(".lirc_select_container").hide();
        }
        clearLircDetail();
    }

}

/**
 * Shows LIRC detail according to selected Model
 * @param vendor_id
 */
function showLircDetailByModel(model_id) {
    myAjaxCall("index.html", {method:"showLircDetailByModel",modelId:model_id}, function (html) {
        fillLircDetail(html);
    });
}

/**
 * Shows LIRC detail according to selected RemoteSection
 * @param vendor_id
 */
function showLircDetailsByRemote(remote_id) {
    if (remote_id != 0) {
        myAjaxCall("index.html", {method:"showLircDetailByRemoteSection",sectionId:remote_id}, function (html) {
            fillLircDetail(html);

        });
    } else {
        clearLircDetail();
    }

}

/**
 * Add select html to page
 * @param html
 */
function addSelectToPage(html, target) {
    $(target).show();
    $(target).find(".select_wrapper").html("");
    $(html).appendTo($(target).find(".select_wrapper"));
    $(".filter_input").attr('tabindex', function () {
        return $(".filter_input").index($(this)) + 1;
    });

}

/**
 *  Adds LIRC details component to page
 * @param html
 */
function fillLircDetail(html) {
    clearLircDetail();
    $("#lirc_details_container").show().html(html);
    bindDetailTitleEvent();
    fillTableInEven();
}

/**
 * paint table with even color
 */
function fillTableInEven() {
    $(".data_table").find("tr:even").addClass("rowEven");
    $(".data_table").find("tr:odd").addClass("rowOdd");
}

/**
 * Clears LIRC details html
 */
function clearLircDetail() {
    $("#lirc_details_container").hide().empty();
}

function showWaiting() {
    $("#waiting_div").show();
}

function hideWaiting() {
    $("#waiting_div").hide();
}

function myAjaxCall(url, data, callback) {
    $.ajax({
        type:"GET",
        url:url,
        success:function(data) {
            callback(data);
            hideWaiting();
        },
        beforeSend:function() {
            showWaiting();
        },
        data:data
    });
}