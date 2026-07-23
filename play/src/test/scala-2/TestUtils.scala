package play.modules.jinbe

object TestUtils {

  def bindingKey(
      name: String
    ): play.api.inject.BindingKey[io.github.cchantep.jinbe.ObjectStorage] =
    JinbeModule.key(name)
}
