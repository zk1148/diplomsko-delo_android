using Newtonsoft.Json;

namespace RVK
{
    public class IzdajaFaknumat
    {
        [JsonProperty("skl")]
        public string Skl { get; set; }

        [JsonProperty("vt")]
        public string Vt { get; set; }

        [JsonProperty("delnalog")]
        public string Delnalog { get; set; }

        [JsonProperty("uporabnik")]
        public string Uporabnik { get; set; }

    }
}
