import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.UITextAlignmentCenter
import platform.UIKit.UIViewController

class TestUIViewController : UIViewController(nibName = null, bundle = null) {
    val messageLabel = UILabel()

    override fun viewDidLoad() {
        super.viewDidLoad()
        // 设置界面元素的属性
        messageLabel.text = "iOS UIViewController界面"
        messageLabel.textAlignment = UITextAlignmentCenter
        messageLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(messageLabel)
        view.backgroundColor = UIColor.whiteColor
        val constraints = listOf(
            messageLabel.centerXAnchor.constraintEqualToAnchor(view.centerXAnchor),
            messageLabel.centerYAnchor.constraintEqualToAnchor(view.centerYAnchor),
        )
        // 设置界面元素的布局约束
        NSLayoutConstraint.activateConstraints(constraints)
    }
}