using Newtonsoft.Json;

namespace RVK
{
    public class IzdajaFakbumat
    {
        [JsonProperty("skl")]
        public string Skl { get; set; }

        [JsonProperty("vt")]
        public string Vt { get; set; }

        [JsonProperty("delnalog")]
        public string Delnalog { get; set; }

        [JsonProperty("stev")]
        public string Stev { get; set; }

        [JsonProperty("arti")]
        public string Arti { get; set; }

        [JsonProperty("koli")]
        public string Koli { get; set; }

        [JsonProperty("em")]
        public string Em { get; set; }

    }
}
