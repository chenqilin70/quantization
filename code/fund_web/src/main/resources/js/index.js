/**
 * Created by aierxuan on 2019-01-25.
 */
$(document).ready(function(){
    var $searchInput= $('#searchInput');
    var $searchMenu=$("#searchMenu")
    $searchInput.on('input propertychange', function() {
        var inputParam = $(this).val();
        $.ajax({
            url:$('#contextPath').val()+"/index/searchTips",
            async:true,
            data:{
                "searchWord":inputParam
            },
            success:function(result){
                $searchMenu.html(result)
                if(result.trim().length!=0){
                    $searchMenu.css("display","block");
                }else{
                    $searchMenu.css("display","none");
                }

            }
        })
    });
    $searchInput.blur(function(){
        $searchMenu.css("display","none");
    })
    $searchInput.focus(function(){
        if($searchMenu.children("li").length!=0){
            $searchMenu.css("display","block")
        }else{
            $searchMenu.css("display","none")
        }
        console.log($searchMenu.children("li").length)
    })
});