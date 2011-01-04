/**
* TODO: 検索、ソート
* 
* scheme of note
* {
*   "title_":"hogehog",
*   "detail":"asdfasdf",
*   "_docId":"e7AKVnagHWmO9QV0mK5qKfRQ1uNWY9S2",
*   "_updatedAt":1289026769886,
*   "_createdAt":1289026170648
* }
*/
var note = {
    save_note : function(){
        $('#save').click(function() {
            // 保持していた docId を参照する
            var docId = $.data($('#main').get(0), 'docId');

			if(!docId) {
				// ノートが選ばれていなかったら。
				alert('choise a note before save!');
				return false;
			};

            // 保存するデータを取得する
            var updateData = {
                'title_': $('#title').val(),
                'detail': $('#detail').val()
            };

            // PUT メソッドで更新する。
            je.PUT('notes', docId, updateData, function() {
                note.list_note();
            });

        });
    },

    delete_note : function() {
        $('#delete').click(function() {
            // 保持していた docId を参照する
            var docId = $.data($('#main').get(0), 'docId');

			if(!docId) {
				// ノートが選ばれていなかったら。
				alert('choise a note before delete!');
				return false;
			};

            // docId のデータを削除する。
            je.DELETE('notes', docId, function() {
                $('#' + docId).parent('li').remove(); // li を消す
                $('#title').val(''); // title を空に
                $('#detail').val(''); // detail を空に
            });
        });
    },

    new_note : function() {
        $('#new').click(function() {
            je.POST('notes', {'title_': 'new note', 'detail': ''},function(note) {
                var a = $('<a>')
                    .attr('href', note._docId) // リンクを docId に
                    .attr('id', note._docId) // id に docId を保持
                    .text(note.title_); // タイトルを表示

                // li 要素作成
                var li = $('<li>').append(a); // li に 作成した a wo

                // li を #note-list に追加
                li.appendTo('#note-list');

                // title, detail を表示
                $('#title').val(note.title_);
                $('#detail').val(note.detail);
                // 現在表示している docId を #main に保持しておく
                $.data($('#main').get(0), 'docId', note._docId);
            });
        });
    },
    
    show_note : function(){
        $('#note-list li a').live('click', function() {
            // id に保持している docId を元に データを取得
            je.GET('notes', this.id, function(note) {
                // 表示
                $('#title').val(note.title_);
                $('#detail').val(note.detail);

                // 現在表示している docId を #main に保持しておく
                $.data($('#main').get(0), 'docId', note._docId);
            });
            return false;
        });
    },

    list_note : function() {
        je.GET('notes', function(notes) {
            // 初期化
            $('#note-list').empty();
            for (var i = 0; i < notes.length; i++) {
                var a = $('<a>')
                    .attr('href', notes[i]._docId) // リンクを docId に
                    .attr('id', notes[i]._docId) // id に docId を保持
                    .text(notes[i].title_); // タイトルを表示

                // li 要素作成
                var li = $('<li>').append(a); // li に 作成した a を追加

                // li を #note-list に追加
                li.appendTo('#note-list');
            }
        });
    },


	search_note : function() {
		$('#search').bind('click', function() {
			// キーワードを取得
			var keyword = $('#keyword').val();
			var params;

			// キーワードが無かったら
			if (!keyword) { 
				alert('input keyword for search!'); 
			} else {
				params = { 'cond' : "title_.eq." + keyword };
			};
			
			je.GET('notes', '', function(notes) {
				// 初期化
				$('#note-list').empty();
				for (var i = 0; i < notes.length; i++) {
					var a = $('<a>')
						.attr('href', notes[i]._docId) // リンクを docId に
						.attr('id', notes[i]._docId) // id に docId を保持
						.text(notes[i].title_); // タイトルを表示
					
					// li 要素作成
					var li = $('<li>').append(a); // li に 作成した a を追加
					
					// li を #note-list に追加
					li.appendTo('#note-list');
				};
			}, params );
		});
	},

    clear_note : function() {
        $('#title').val('');
        $('#detail').val('');
    }

};

$(function() {
    note.clear_note();
    note.save_note();
    note.new_note();
    note.delete_note();
    note.list_note();
    note.show_note();
	note.search_note();
});


