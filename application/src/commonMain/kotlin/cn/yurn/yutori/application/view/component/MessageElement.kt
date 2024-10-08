package cn.yurn.yutori.application.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.Message
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.resize.SizeResolver
import kotlin.math.min
import kotlin.math.roundToInt

abstract class MessageElementViewer<T : MessageElement> {
    abstract fun preview(element: T): String

    @Composable
    abstract fun Content(element: T)

    @Composable
    abstract fun ContentInQuote(element: T)
}

object TextMessageElementViewer : MessageElementViewer<Text>() {
    override fun preview(element: Text) = element.text

    @Composable
    override fun Content(element: Text) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Text) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object AtMessageElementViewer : MessageElementViewer<At>() {
    override fun preview(element: At) = "@${element.name}"

    @Composable
    override fun Content(element: At) {
        Text(
            text = preview(element),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: At) {
        Text(
            text = preview(element),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object SharpMessageElementViewer : MessageElementViewer<Sharp>() {
    override fun preview(element: Sharp) = "#${element.name}"

    @Composable
    override fun Content(element: Sharp) {
        Text(
            text = preview(element),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Sharp) {
        Text(
            text = preview(element),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object HrefMessageElementViewer : MessageElementViewer<Href>() {
    override fun preview(element: Href) = "[链接]"

    @Composable
    override fun Content(element: Href) {
        Text(
            text = element.children.joinToString(""),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.Underline
        )
    }

    @Composable
    override fun ContentInQuote(element: Href) {
        Text(
            text = element.children.joinToString(""),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline
        )
    }
}

object ImageMessageElementViewer : MessageElementViewer<Image>() {
    override fun preview(element: Image) = "[图片]"

    @Composable
    override fun Content(element: Image) {
        val state = rememberAsyncImageState()
        val loadState = state.loadState
        var sizeResolver by remember { mutableStateOf<SizeResolver?>(null) }
        var visible by remember { mutableStateOf(true) }
        if (loadState is LoadState.Success) {
            visible = false
            val image = loadState.result.image
            val constWidth = 600
            val constHeight = 720
            val ratio = min(
                constWidth / image.width.toDouble(),
                constHeight / image.height.toDouble()
            )
            sizeResolver = SizeResolver(
                (ratio * image.width).roundToInt(),
                (ratio * image.height).roundToInt()
            )
        }
        AsyncImage(
            request = ComposableImageRequest(element.src) {
                resize(size = sizeResolver)
                pauseLoadWhenScrolling(true)
            },
            contentDescription = null,
            state = state,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .placeholder(
                    visible = visible,
                    highlight = PlaceholderHighlight.shimmer()
                )
        )
    }

    @Composable
    override fun ContentInQuote(element: Image) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object AudioMessageElementViewer : MessageElementViewer<Audio>() {
    override fun preview(element: Audio) = "[语音]"

    @Composable
    override fun Content(element: Audio) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Audio) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object VideoMessageElementViewer : MessageElementViewer<Video>() {
    override fun preview(element: Video) = "[视频]"

    @Composable
    override fun Content(element: Video) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Video) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object FileMessageElementViewer : MessageElementViewer<File>() {
    override fun preview(element: File) = "[文件]"

    @Composable
    override fun Content(element: File) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: File) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object BoldMessageElementViewer : MessageElementViewer<Bold>() {
    override fun preview(element: Bold) = element.children.joinToString("")

    @Composable
    override fun Content(element: Bold) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    override fun ContentInQuote(element: Bold) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

object StrongMessageElementViewer : MessageElementViewer<Strong>() {
    override fun preview(element: Strong) = element.children.joinToString("")

    @Composable
    override fun Content(element: Strong) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    override fun ContentInQuote(element: Strong) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

object IdiomaticMessageElementViewer : MessageElementViewer<Idiomatic>() {
    override fun preview(element: Idiomatic) = element.children.joinToString("")

    @Composable
    override fun Content(element: Idiomatic) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic
        )
    }

    @Composable
    override fun ContentInQuote(element: Idiomatic) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

object EmMessageElementViewer : MessageElementViewer<Em>() {
    override fun preview(element: Em) = element.children.joinToString("")

    @Composable
    override fun Content(element: Em) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic
        )
    }

    @Composable
    override fun ContentInQuote(element: Em) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

object UnderlineMessageElementViewer : MessageElementViewer<Underline>() {
    override fun preview(element: Underline) = element.children.joinToString("")

    @Composable
    override fun Content(element: Underline) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.Underline
        )
    }

    @Composable
    override fun ContentInQuote(element: Underline) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline
        )
    }
}

object InsMessageElementViewer : MessageElementViewer<Ins>() {
    override fun preview(element: Ins) = element.children.joinToString("")

    @Composable
    override fun Content(element: Ins) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.Underline
        )
    }

    @Composable
    override fun ContentInQuote(element: Ins) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline
        )
    }
}

object StrikethroughMessageElementViewer : MessageElementViewer<Strikethrough>() {
    override fun preview(element: Strikethrough) = element.children.joinToString("")

    @Composable
    override fun Content(element: Strikethrough) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.LineThrough
        )
    }

    @Composable
    override fun ContentInQuote(element: Strikethrough) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.LineThrough
        )
    }
}

object DeleteMessageElementViewer : MessageElementViewer<Delete>() {
    override fun preview(element: Delete) = element.children.joinToString("")

    @Composable
    override fun Content(element: Delete) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.LineThrough
        )
    }

    @Composable
    override fun ContentInQuote(element: Delete) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.LineThrough
        )
    }
}

object SplMessageElementViewer : MessageElementViewer<Spl>() {
    override fun preview(element: Spl) = "[剧透]"

    @Composable
    override fun Content(element: Spl) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Spl) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object CodeMessageElementViewer : MessageElementViewer<Code>() {
    override fun preview(element: Code) = "[代码]"

    @Composable
    override fun Content(element: Code) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Code) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object SupMessageElementViewer : MessageElementViewer<Sup>() {
    override fun preview(element: Sup) = "[上标]"

    @Composable
    override fun Content(element: Sup) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Sup) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object SubMessageElementViewer : MessageElementViewer<Sub>() {
    override fun preview(element: Sub) = "[下标]"

    @Composable
    override fun Content(element: Sub) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Sub) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object BrMessageElementViewer : MessageElementViewer<Br>() {
    override fun preview(element: Br) = ""

    @Composable
    override fun Content(element: Br) {
        Text(text = preview(element))
    }

    @Composable
    override fun ContentInQuote(element: Br) {
        Text(text = preview(element))
    }
}

object ParagraphMessageElementViewer : MessageElementViewer<Paragraph>() {
    override fun preview(element: Paragraph) = "[段落]"

    @Composable
    override fun Content(element: Paragraph) {
        Text(
            text = element.children.joinToString(""),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Paragraph) {
        Text(
            text = element.children.joinToString(""),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object MessageMessageElementViewer : MessageElementViewer<Message>() {
    override fun preview(element: Message) = "[消息]"

    @Composable
    override fun Content(element: Message) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Message) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object QuoteMessageElementViewer : MessageElementViewer<Quote>() {
    override fun preview(element: Quote) = "[引用]"

    @Composable
    override fun Content(element: Quote) {
        Surface(
            color = Color(0, 0, 0, 50),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(8.dp, 4.dp)
            ) {
                for (child in element.children) when (child) {
                    is Text -> TextMessageElementViewer.ContentInQuote(child)
                    is At -> AtMessageElementViewer.ContentInQuote(child)
                    is Sharp -> SharpMessageElementViewer.ContentInQuote(child)
                    is Href -> HrefMessageElementViewer.ContentInQuote(child)
                    is Image -> ImageMessageElementViewer.ContentInQuote(child)
                    is Audio -> AudioMessageElementViewer.ContentInQuote(child)
                    is Video -> VideoMessageElementViewer.ContentInQuote(child)
                    is File -> FileMessageElementViewer.ContentInQuote(child)
                    is Bold -> BoldMessageElementViewer.ContentInQuote(child)
                    is Strong -> StrongMessageElementViewer.ContentInQuote(child)
                    is Idiomatic -> IdiomaticMessageElementViewer.ContentInQuote(child)
                    is Em -> EmMessageElementViewer.ContentInQuote(child)
                    is Underline -> UnderlineMessageElementViewer.ContentInQuote(child)
                    is Ins -> InsMessageElementViewer.ContentInQuote(child)
                    is Strikethrough -> StrikethroughMessageElementViewer.ContentInQuote(child)
                    is Delete -> DeleteMessageElementViewer.ContentInQuote(child)
                    is Spl -> SplMessageElementViewer.ContentInQuote(child)
                    is Code -> CodeMessageElementViewer.ContentInQuote(child)
                    is Sup -> SupMessageElementViewer.ContentInQuote(child)
                    is Sub -> SubMessageElementViewer.ContentInQuote(child)
                    is Br -> BrMessageElementViewer.ContentInQuote(child)
                    is Paragraph -> ParagraphMessageElementViewer.ContentInQuote(child)
                    is Message -> MessageMessageElementViewer.ContentInQuote(child)
                    is Quote -> ContentInQuote(child)
                    is Author -> AuthorMessageElementViewer.ContentInQuote(child)
                    is Button -> ButtonMessageElementViewer.ContentInQuote(child)
                    else -> UnsupportedMessageElementViewer.ContentInQuote(child)
                }
            }
        }
    }

    @Composable
    override fun ContentInQuote(element: Quote) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object AuthorMessageElementViewer : MessageElementViewer<Author>() {
    override fun preview(element: Author) = "[作者]"

    @Composable
    override fun Content(element: Author) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Author) {
        Text(
            text = element.name ?: "null",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object ButtonMessageElementViewer : MessageElementViewer<Button>() {
    override fun preview(element: Button) = "[按钮]"

    @Composable
    override fun Content(element: Button) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: Button) {
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

object UnsupportedMessageElementViewer : MessageElementViewer<MessageElement>() {
    override fun preview(element: MessageElement): String {
        return "[未支持的消息]"
    }

    @Composable
    override fun Content(element: MessageElement) {
        if (element is NodeMessageElement) {
            if (element.children.size > 0) {
                for (child in element.children) {
                    when (child) {
                        is Text -> TextMessageElementViewer.apply { Content(child) }
                        is At -> AtMessageElementViewer.apply { Content(child) }
                        is Sharp -> SharpMessageElementViewer.apply { Content(child) }
                        is Href -> HrefMessageElementViewer.apply { Content(child) }
                        is Image -> ImageMessageElementViewer.apply { Content(child) }
                        is Audio -> AudioMessageElementViewer.apply { Content(child) }
                        is Video -> VideoMessageElementViewer.apply { Content(child) }
                        is File -> FileMessageElementViewer.apply { Content(child) }
                        is Bold -> BoldMessageElementViewer.apply { Content(child) }
                        is Strong -> StrongMessageElementViewer.apply { Content(child) }
                        is Idiomatic -> IdiomaticMessageElementViewer.apply { Content(child) }
                        is Em -> EmMessageElementViewer.apply { Content(child) }
                        is Underline -> UnderlineMessageElementViewer.apply { Content(child) }
                        is Ins -> InsMessageElementViewer.apply { Content(child) }
                        is Strikethrough -> StrikethroughMessageElementViewer.apply { Content(child) }
                        is Delete -> DeleteMessageElementViewer.apply { Content(child) }
                        is Spl -> SplMessageElementViewer.apply { Content(child) }
                        is Code -> CodeMessageElementViewer.apply { Content(child) }
                        is Sup -> SupMessageElementViewer.apply { Content(child) }
                        is Sub -> SubMessageElementViewer.apply { Content(child) }
                        is Br -> BrMessageElementViewer.apply { Content(child) }
                        is Paragraph -> ParagraphMessageElementViewer.apply { Content(child) }
                        is Message -> MessageMessageElementViewer.apply { Content(child) }
                        is Quote -> QuoteMessageElementViewer.apply { Content(child) }
                        is Author -> AuthorMessageElementViewer.apply { Content(child) }
                        is Button -> ButtonMessageElementViewer.apply { Content(child) }
                        else -> Content(child)
                    }
                }
                return
            }
        }
        Text(
            text = "Unsupported element: $element",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    @Composable
    override fun ContentInQuote(element: MessageElement) {
        Text(
            text = preview(element),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}