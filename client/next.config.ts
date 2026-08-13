import type { NextConfig } from "next";

require('dotenv')
    .config({ path: '../.env' })

const nextConfig: NextConfig = {
  output: "standalone",
};

export default nextConfig;
