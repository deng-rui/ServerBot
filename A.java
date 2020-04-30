this.main.getEventListener().subscribeAlways(TempMessage.class, (event) -> {
            if (toString(event.getMessage()).contains(".bind")) {
                // 引用回复
                final QuoteReply quote = MessageUtils.quote(event.getMessage());
                event.getSender().sendMessage(quote.plus("引用回复"));

            } else if (toString(event.getMessage()).contains("friend")) {
                final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync("Async send"); // 异步发送
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                //将图片转换为图片ID
            }
        });

        this.main.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            if (toString(event.getMessage()).contains("reply")) {
                // 引用回复
                final QuoteReply quote = MessageUtils.quote(event.getMessage());
                event.getGroup().sendMessage(quote.plus("引用回复"));

            } else if (toString(event.getMessage()).contains("at")) {
                // at
                event.getGroup().sendMessage(new At(event.getSender()));

            } else if (toString(event.getMessage()).contains("permission")) {
                // 成员权限
                event.getGroup().sendMessage(event.getPermission().toString());

            } else if (toString(event.getMessage()).contains("mixed")) {
                // 复合消息, 通过 .plus 连接两个消息
                event.getGroup().sendMessage(
                        MessageUtils.newImage("{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.png") // 演示图片, 可能已过期
                                .plus("Hello") // 文本消息
                                .plus(new At(event.getSender())) // at 群成员
                                .plus(AtAll.INSTANCE) // at 全体成员
                );

            } else if (toString(event.getMessage()).contains("recall1")) {
                event.getGroup().sendMessage("你看不到这条消息").recall();
                // 发送消息马上就撤回. 因速度太快, 客户端将看不到这个消息.

            } else if (toString(event.getMessage()).contains("上传图片")) {
                File file = new File("myImage.jpg");
                if (file.exists()) {
                    final Image image = event.getGroup().uploadImage(new File("myImage.jpg"));
                    // 上传一个图片并得到 Image 类型的 Message

                    final String imageId = image.getImageId(); // 可以拿到 ID
                    final Image fromId = MessageUtils.newImage(imageId); // ID 转换得到 Image

                    event.getGroup().sendMessage(image); // 发送图片
                }

            } else if (toString(event.getMessage()).contains("friend")) {
                final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync("Async send"); // 异步发送
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                //将图片转换为图片ID
            } else if (toString(event.getMessage()).contains("id")) {
                // at
                event.getGroup().sendMessage(String.valueOf(event.getGroup().getId()));

            } else if (toString(event.getMessage()).contains("qq")) {
                // at
                event.getGroup().sendMessage(String.valueOf(event.getSender().getId()));

            } 
        });