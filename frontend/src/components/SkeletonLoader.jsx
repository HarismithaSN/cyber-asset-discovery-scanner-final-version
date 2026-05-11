import React, { useState } from "react";

/**
 * Skeleton Loader Component
 * Displays a loading skeleton to improve perceived performance
 * Used while async data is being fetched
 */
export const SkeletonAsset = () => (
  <div className="bg-white rounded-2xl shadow p-6 border border-slate-200 animate-pulse">
    <div className="flex justify-between items-start gap-3 mb-4">
      <div>
        <div className="h-6 bg-slate-300 rounded w-48 mb-2"></div>
        <div className="h-4 bg-slate-200 rounded w-24"></div>
      </div>
      <div className="h-8 bg-slate-300 rounded-full w-20"></div>
    </div>

    <div className="space-y-2">
      <div className="h-4 bg-slate-200 rounded w-full"></div>
      <div className="h-4 bg-slate-200 rounded w-full"></div>
      <div className="h-4 bg-slate-200 rounded w-3/4"></div>
    </div>

    <div className="mt-5 flex flex-wrap gap-3">
      <div className="h-10 bg-slate-300 rounded w-20"></div>
      <div className="h-10 bg-slate-300 rounded w-20"></div>
      <div className="h-10 bg-slate-300 rounded w-20"></div>
    </div>
  </div>
);

/**
 * Skeleton Container for multiple assets
 */
export const SkeletonAssetList = () => (
  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
    {[1, 2, 3, 4].map((i) => (
      <SkeletonAsset key={i} />
    ))}
  </div>
);

/**
 * Skeleton Table Row for dashboard
 */
export const SkeletonTableRow = () => (
  <tr className="bg-white hover:bg-gray-50 border-b animate-pulse">
    <td className="px-6 py-4">
      <div className="h-4 bg-slate-300 rounded w-32"></div>
    </td>
    <td className="px-6 py-4">
      <div className="h-4 bg-slate-300 rounded w-24"></div>
    </td>
    <td className="px-6 py-4">
      <div className="h-4 bg-slate-300 rounded w-28"></div>
    </td>
    <td className="px-6 py-4">
      <div className="h-6 bg-slate-300 rounded-full w-16"></div>
    </td>
    <td className="px-6 py-4">
      <div className="h-6 bg-slate-300 rounded w-12"></div>
    </td>
  </tr>
);

/**
 * Reusable Skeleton Loader with custom height
 */
export const SkeletonBase = ({ height = "h-4", width = "w-full", count = 1 }) => (
  <div className="space-y-2">
    {Array.from({ length: count }).map((_, i) => (
      <div key={i} className={`bg-slate-300 rounded ${height} ${width} animate-pulse`}></div>
    ))}
  </div>
);